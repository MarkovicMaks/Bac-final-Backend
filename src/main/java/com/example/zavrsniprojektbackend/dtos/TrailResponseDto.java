package com.example.zavrsniprojektbackend.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record TrailResponseDto(
        Integer id,
        String name,
        String description,
        BigDecimal lengthKm,
        BigDecimal heightKm,
        String difficulty,
        Instant createdAt,
        BigDecimal minElevation,
        BigDecimal maxElevation,
        BigDecimal totalAscent,
        BigDecimal totalDescent,
        List<WaypointDto> waypoints,
        BiomePercentagesDto biomes,
        RatingStatsDto ratingStats
) {
    // Keep existing constructors for compatibility
    public TrailResponseDto(
            Integer id,
            String name,
            String description,
            BigDecimal lengthKm,
            BigDecimal heightKm,
            String difficulty,
            Instant createdAt,
            BigDecimal minElevation,
            BigDecimal maxElevation,
            BigDecimal totalAscent,
            BigDecimal totalDescent,
            List<WaypointDto> waypoints,
            BiomePercentagesDto biomes) {
        this(id, name, description, lengthKm, heightKm, difficulty, createdAt,
                minElevation, maxElevation, totalAscent, totalDescent, waypoints, biomes, null);
    }
}