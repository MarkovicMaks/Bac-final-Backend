package com.example.zavrsniprojektbackend.dtos;

import java.math.BigDecimal;

public record WaypointDto(
        double latitude,
        double longitude,
        int order,
        BigDecimal elevation  // Dodano za visinske podatke
) {
    // Constructor bez elevation za compatibility s postojećim kodom
    public WaypointDto(double latitude, double longitude, int order) {
        this(latitude, longitude, order, null);
    }
}