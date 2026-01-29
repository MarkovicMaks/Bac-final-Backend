package com.example.zavrsniprojektbackend.services;

import com.example.zavrsniprojektbackend.dtos.*;
import com.example.zavrsniprojektbackend.models.*;
import com.example.zavrsniprojektbackend.repos.TrailRepository;
import com.example.zavrsniprojektbackend.repos.TrailBiomeRepository;
import com.example.zavrsniprojektbackend.repos.TrailRatingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrailServiceImpl implements TrailService {

    private final TrailRepository trailRepo;
    private final TrailBiomeRepository trailBiomeRepo;
    private final TrailRatingRepository trailRatingRepo;
    private final LulcAnalysisService lulcAnalysisService;

    @Override
    @Transactional
    public TrailResponseDto createTrail(CreateTrailRequest req, User user) {  // ADD User parameter here
        log.info("Creating trail: {} for user: {}", req.name(), user.getId());

        Trail trail = Trail.builder()
                .name(req.name())
                .description(req.description())
                .lengthKm(req.lengthKm())
                .heightKm(req.heightKm())
                .difficulty(req.difficulty())
                .createdBy(user)  // ADD this line
                .build();

        // Add waypoints
        req.waypoints().forEach(wpDto -> {
            TrailWaypoint wp = TrailWaypoint.builder()
                    .latitude(BigDecimal.valueOf(wpDto.latitude()))
                    .longitude(BigDecimal.valueOf(wpDto.longitude()))
                    .order(wpDto.order())
                    .elevation(wpDto.elevation())
                    .build();
            trail.addWaypoint(wp);
        });

        // Calculate elevation statistics
        trail.calculateElevationStats();

        // Save trail
        Trail saved = trailRepo.save(trail);
        log.info("Trail saved with ID: {}", saved.getId());

        // Start async LULC analysis
        analyzeLulcAsync(saved);

        return mapToTrailResponseDto(saved);
    }

    @Async
    protected void analyzeLulcAsync(Trail trail) {
        try {
            log.info("Starting async LULC analysis for trail: {}", trail.getId());
            lulcAnalysisService.analyzeTrailLulc(trail);
            log.info("Completed LULC analysis for trail: {}", trail.getId());
        } catch (Exception e) {
            log.error("Error analyzing LULC for trail {}: {}", trail.getId(), e.getMessage(), e);
        }
    }
    @Transactional
    public void deleteTrail(Integer trailId, User user) {
        Trail trail = trailRepo.findById(trailId)
                .orElseThrow(() -> new RuntimeException("Trail not found with ID: " + trailId));

        // Check if user owns this trail
        if (!trail.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own trails");
        }

        trailRepo.delete(trail);
        log.info("Trail {} deleted by user {}", trailId, user.getId());
    }
    @Override
    public List<TrailResponseDto> getAllTrails() {
        return trailRepo.findAll().stream()
                .map(this::mapToTrailResponseDto)
                .toList();
    }

    @Override
    public TrailResponseDto getTrailById(Integer id) {
        Trail trail = trailRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Trail not found with ID: " + id));
        return mapToTrailResponseDto(trail);
    }

    @Override
    public List<TrailResponseDto> getTrailsByUser(Integer userId) {
        return trailRepo.findByCreatedById(userId).stream()
                .map(this::mapToTrailResponseDto)
                .toList();
    }

    private TrailResponseDto mapToTrailResponseDto(Trail trail) {
        return mapToTrailResponseDto(trail, null);
    }

    private TrailResponseDto mapToTrailResponseDto(Trail trail, Integer currentUserId) {
        // Map waypoints
        var wpDtos = trail.getWaypoints().stream()
                .sorted(Comparator.comparingInt(TrailWaypoint::getOrder))
                .map(w -> new WaypointDto(
                        w.getLatitude().doubleValue(),
                        w.getLongitude().doubleValue(),
                        w.getOrder(),
                        w.getElevation()
                ))
                .toList();

        // Get biome data if available
        BiomePercentagesDto biomes = trailBiomeRepo.findByTrailId(trail.getId())
                .map(this::mapToBiomeDto)
                .orElse(null);

        // Get rating stats
        RatingStatsDto ratingStats = getRatingStats(trail.getId(), currentUserId);

        // Map creator info
        UserInfoDto createdBy = null;
        if (trail.getCreatedBy() != null) {
            createdBy = new UserInfoDto(
                    trail.getCreatedBy().getId(),
                    trail.getCreatedBy().getFullName(),
                    trail.getCreatedBy().getEmail(),
                    trail.getCreatedBy().getRole().name()
            );
        }

        return new TrailResponseDto(
                trail.getId(),
                trail.getName(),
                trail.getDescription(),
                trail.getLengthKm(),
                trail.getHeightKm(),
                trail.getDifficulty(),
                trail.getCreatedAt(),
                trail.getMinElevation(),
                trail.getMaxElevation(),
                trail.getTotalAscent(),
                trail.getTotalDescent(),
                wpDtos,
                biomes,
                ratingStats,
                createdBy  // NEW: Include creator info
        );
    }

    private RatingStatsDto getRatingStats(Integer trailId, Integer userId) {
        Double avgRating = trailRatingRepo.getAverageRatingForTrail(trailId);
        Long totalRatings = trailRatingRepo.getCountForTrail(trailId);

        Integer userRating = null;
        if (userId != null) {
            userRating = trailRatingRepo.findByTrailIdAndUserId(trailId, userId)
                    .map(TrailRating::getRating)
                    .orElse(null);
        }

        return new RatingStatsDto(avgRating, totalRatings, userRating);
    }

    private BiomePercentagesDto mapToBiomeDto(TrailBiome biome) {
        return new BiomePercentagesDto(
                biome.getZimzelenaPercentage(),
                biome.getListopadnaPercentage(),
                biome.getLivadePercentage(),
                biome.getUrbanoPercentage(),
                biome.getPoljaPercentage(),
                biome.getVodePercentage(),
                biome.getDominantBiome(),
                biome.getAnalyzedAt()
        );
    }
}