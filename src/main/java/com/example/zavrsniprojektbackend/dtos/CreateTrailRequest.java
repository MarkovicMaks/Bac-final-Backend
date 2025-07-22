package com.example.zavrsniprojektbackend.dtos;


import java.math.BigDecimal;
import java.util.List;

public record CreateTrailRequest(
        String name,
        String description,
        BigDecimal lengthKm,
        BigDecimal heightKm,
        String difficulty,
        List<WaypointDto> waypoints) {}

