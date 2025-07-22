package com.example.zavrsniprojektbackend.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record TrailResponseDto(
        Integer id,
        String name,
        BigDecimal lengthKm,
        BigDecimal heightKm,
        String difficulty,
        Instant createdAt,
        List<WaypointDto> waypoints) {}



