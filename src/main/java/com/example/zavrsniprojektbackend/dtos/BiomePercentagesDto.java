package com.example.zavrsniprojektbackend.dtos;

import java.math.BigDecimal;
import java.time.Instant;

public record BiomePercentagesDto(
        BigDecimal zimzelenaPercentage,
        BigDecimal listopadnaPercentage,
        BigDecimal livadePercentage,
        BigDecimal urbanoPercentage,
        BigDecimal poljaPercentage,
        BigDecimal vodePercentage,
        String dominantBiome,
        Instant analyzedAt
) {}