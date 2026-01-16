package com.example.zavrsniprojektbackend.dtos;

public record RatingStatsDto(
        Double averageRating,
        Long totalRatings,
        Integer userRating
) {}