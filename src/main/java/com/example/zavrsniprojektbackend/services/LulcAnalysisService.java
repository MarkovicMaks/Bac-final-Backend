package com.example.zavrsniprojektbackend.services;

import com.example.zavrsniprojektbackend.models.Trail;
import com.example.zavrsniprojektbackend.models.TrailBiome;
import com.example.zavrsniprojektbackend.models.TrailWaypoint;
import com.example.zavrsniprojektbackend.repos.TrailBiomeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LulcAnalysisService {

    private final TrailBiomeRepository trailBiomeRepository;

    @Autowired
    private DataSource dataSource;

    @Value("${postgis.buffer.distance:15}")
    private double bufferDistance;

    @Value("${postgis.srid:32633}")
    private int srid;

    @Value("${postgis.raster.table:lulc_raster}")
    private String rasterTable;

    @Transactional
    public TrailBiome analyzeTrailLulc(Trail trail) {
        log.info("Starting LULC analysis for trail: {} with {} waypoints",
                trail.getName(), trail.getWaypoints().size());

        if (trail.getWaypoints() == null || trail.getWaypoints().isEmpty()) {
            log.warn("Trail {} has no waypoints", trail.getName());
            return createEmptyBiomeAnalysis(trail);
        }

        try {
            Map<Integer, Integer> totalLulcCounts = new HashMap<>();
            int totalPixels = 0;

            List<TrailWaypoint> sortedWaypoints = trail.getWaypoints().stream()
                    .sorted(Comparator.comparingInt(TrailWaypoint::getOrder))
                    .toList();

            // Analyze each waypoint
            for (TrailWaypoint waypoint : sortedWaypoints) {
                Map<Integer, Integer> waypointCounts = analyzeSingleWaypoint(waypoint);

                // Aggregate counts
                for (Map.Entry<Integer, Integer> entry : waypointCounts.entrySet()) {
                    totalLulcCounts.merge(entry.getKey(), entry.getValue(), Integer::sum);
                    totalPixels += entry.getValue();
                }
            }

            log.info("Analysis complete - Total pixels: {}, LULC distribution: {}",
                    totalPixels, totalLulcCounts);

            // Calculate percentages and save
            TrailBiome biomeAnalysis = calculateBiomePercentages(trail, totalLulcCounts, totalPixels);
            return trailBiomeRepository.save(biomeAnalysis);

        } catch (Exception e) {
            log.error("Error analyzing trail LULC: ", e);
            return createEmptyBiomeAnalysis(trail);
        }
    }

    /**
     * Analyze a single waypoint against the LULC raster
     * Returns map of LULC class -> pixel count
     */
    private Map<Integer, Integer> analyzeSingleWaypoint(TrailWaypoint waypoint) {
        Map<Integer, Integer> lulcCounts = new HashMap<>();

        // Convert lat/lon to projected coordinates (assuming your raster is in EPSG:32633)
        // You need to transform WGS84 (4326) to your SRID (32633)
        double lon = waypoint.getLongitude().doubleValue();
        double lat = waypoint.getLatitude().doubleValue();

        String sql = """
            WITH p AS (
                -- Transform WGS84 point to raster SRID
                SELECT ST_Transform(
                    ST_SetSRID(ST_Point(?, ?), 4326),
                    ?
                ) AS geom
            ),
            w AS (
                -- Create buffer envelope (3x3 area ~15m buffer for 10m resolution)
                SELECT ST_Envelope(ST_Buffer(geom, ?)) AS geom
                FROM p
            ),
            clipped AS (
                -- Clip raster to buffer area
                SELECT ST_Clip(r.rast, w.geom) AS rast
                FROM %s r
                JOIN w ON ST_Intersects(r.rast, w.geom)
            )
            -- Count pixels per LULC class
            SELECT (vc).value AS lulc_class, SUM((vc).count) AS pixels
            FROM (
                SELECT ST_ValueCount(rast, 1, TRUE) AS vc
                FROM clipped
            ) t
            GROUP BY (vc).value
            ORDER BY lulc_class
            """.formatted(rasterTable);  // Use configured table name

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, lon);
            stmt.setDouble(2, lat);
            stmt.setInt(3, srid);
            stmt.setDouble(4, bufferDistance);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int lulcClass = rs.getInt("lulc_class");
                    int pixels = rs.getInt("pixels");
                    lulcCounts.put(lulcClass, pixels);
                }
            }

            if (!lulcCounts.isEmpty()) {
                log.debug("Waypoint ({}, {}) -> LULC counts: {}", lat, lon, lulcCounts);
            }

        } catch (SQLException e) {
            log.error("Error querying waypoint ({}, {}): {}", lat, lon, e.getMessage());
        }

        return lulcCounts;
    }

    /**
     * Calculate biome percentages from LULC pixel counts
     * Maps your 6 categories to the biome fields
     */
    private TrailBiome calculateBiomePercentages(Trail trail, Map<Integer, Integer> lulcCounts, int totalPixels) {
        TrailBiome biome = TrailBiome.builder()
                .trail(trail)
                .build();

        if (totalPixels == 0) {
            log.warn("No pixels found for trail {}", trail.getName());
            return biome;
        }

        log.info("Calculating percentages - Total pixels: {}, LULC counts: {}", totalPixels, lulcCounts);

        // Map your LULC categories (1-6) to biome fields
        // Adjust these mappings based on your actual LULC classification:
        // 1 = Zimzelena (Evergreen)
        // 2 = Listopadna (Deciduous)
        // 3 = Livade (Grassland/Meadows)
        // 4 = Polja (Fields/Agriculture)
        // 5 = Urbano (Urban)
        // 6 = Vode (Water)

        for (Map.Entry<Integer, Integer> entry : lulcCounts.entrySet()) {
            BigDecimal percentage = BigDecimal.valueOf(entry.getValue())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalPixels), 2, RoundingMode.HALF_UP);

            int lulcClass = entry.getKey();

            switch (lulcClass) {
                case 1 -> {
                    biome.setZimzelenaPercentage(percentage);
                    log.debug("Zimzelena (Evergreen): {}%", percentage);
                }
                case 2 -> {
                    biome.setListopadnaPercentage(percentage);
                    log.debug("Listopadna (Deciduous): {}%", percentage);
                }
                case 3 -> {
                    biome.setLivadePercentage(percentage);
                    log.debug("Livade (Grasslands): {}%", percentage);
                }
                case 4 -> {
                    biome.setPoljaPercentage(percentage);
                    log.debug("Polja (Fields): {}%", percentage);
                }
                case 5 -> {
                    biome.setUrbanoPercentage(percentage);
                    log.debug("Urbano (Urban): {}%", percentage);
                }
                case 6 -> {
                    biome.setVodePercentage(percentage);
                    log.debug("Vode (Water): {}%", percentage);
                }
                default -> log.warn("Unknown LULC class: {}", lulcClass);
            }
        }

        log.info("Biome analysis complete - Dominant: {}", biome.getDominantBiome());
        return biome;
    }

    private TrailBiome createEmptyBiomeAnalysis(Trail trail) {
        return trailBiomeRepository.save(TrailBiome.builder()
                .trail(trail)
                .zimzelenaPercentage(BigDecimal.ZERO)
                .listopadnaPercentage(BigDecimal.ZERO)
                .livadePercentage(BigDecimal.ZERO)
                .urbanoPercentage(BigDecimal.ZERO)
                .poljaPercentage(BigDecimal.ZERO)
                .vodePercentage(BigDecimal.ZERO)
                .build());
    }
}