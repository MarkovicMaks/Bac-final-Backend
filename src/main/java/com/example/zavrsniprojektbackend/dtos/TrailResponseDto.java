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
        BiomePercentagesDto biomes  // Može biti null dok se ne implementira
) {
    // Konstruktor za kompatibilnost s postojećim kodom (bez biomes)
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
            List<WaypointDto> waypoints) {
        this(id, name, description, lengthKm, heightKm, difficulty, createdAt,
                minElevation, maxElevation, totalAscent, totalDescent, waypoints, null);
    }
}