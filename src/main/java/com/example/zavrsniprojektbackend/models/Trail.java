package com.example.zavrsniprojektbackend.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trails")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Trail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "length_km", nullable = false, precision = 10, scale = 2)
    private BigDecimal lengthKm;

    @Column(name = "height_km", nullable = false, precision = 10, scale = 2)
    private BigDecimal heightKm;

    private String difficulty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // Novi elevation podaci
    @Column(name = "min_elevation", precision = 8, scale = 2)
    private BigDecimal minElevation;

    @Column(name = "max_elevation", precision = 8, scale = 2)
    private BigDecimal maxElevation;

    @Column(name = "total_ascent", precision = 8, scale = 2)
    private BigDecimal totalAscent;

    @Column(name = "total_descent", precision = 8, scale = 2)
    private BigDecimal totalDescent;

    @OneToMany(mappedBy = "trail", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TrailWaypoint> waypoints = new ArrayList<>();

    // Helper method za dodavanje waypoint-a
    public void addWaypoint(TrailWaypoint waypoint) {
        waypoints.add(waypoint);
        waypoint.setTrail(this);
    }

    // Method to calculate and set elevation statistics from waypoints
    public void calculateElevationStats() {
        if (waypoints == null || waypoints.isEmpty()) {
            return;
        }

        List<BigDecimal> elevations = waypoints.stream()
                .map(TrailWaypoint::getElevation)
                .filter(elevation -> elevation != null)
                .toList();

        if (elevations.isEmpty()) {
            return;
        }

        // Calculate min/max elevation
        this.minElevation = elevations.stream().min(BigDecimal::compareTo).orElse(null);
        this.maxElevation = elevations.stream().max(BigDecimal::compareTo).orElse(null);

        // Calculate ascent and descent
        BigDecimal ascent = BigDecimal.ZERO;
        BigDecimal descent = BigDecimal.ZERO;

        List<TrailWaypoint> sortedWaypoints = waypoints.stream()
                .filter(wp -> wp.getElevation() != null)
                .sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder()))
                .toList();

        for (int i = 1; i < sortedWaypoints.size(); i++) {
            TrailWaypoint current = sortedWaypoints.get(i);
            TrailWaypoint previous = sortedWaypoints.get(i - 1);

            if (current.getElevation() != null && previous.getElevation() != null) {
                BigDecimal elevationDiff = current.getElevation().subtract(previous.getElevation());

                if (elevationDiff.compareTo(BigDecimal.ZERO) > 0) {
                    ascent = ascent.add(elevationDiff);
                } else {
                    descent = descent.add(elevationDiff.abs());
                }
            }
        }

        this.totalAscent = ascent;
        this.totalDescent = descent;
    }
}