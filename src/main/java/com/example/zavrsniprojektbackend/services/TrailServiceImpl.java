package com.example.zavrsniprojektbackend.services;

import com.example.zavrsniprojektbackend.dtos.CreateTrailRequest;
import com.example.zavrsniprojektbackend.dtos.TrailResponseDto;
import com.example.zavrsniprojektbackend.dtos.WaypointDto;
import com.example.zavrsniprojektbackend.models.Trail;
import com.example.zavrsniprojektbackend.models.TrailWaypoint;
import com.example.zavrsniprojektbackend.repos.TrailRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrailServiceImpl implements TrailService {

    private final TrailRepository trailRepo;

    @Override
    @Transactional
    public TrailResponseDto createTrail(CreateTrailRequest req) {

        Trail trail = Trail.builder()
                .name(req.name())
                .description(req.description())
                .lengthKm(req.lengthKm())
                .heightKm(req.heightKm())
                .difficulty(req.difficulty())
                .build();

        req.waypoints().forEach(wpDto -> {
            TrailWaypoint wp = TrailWaypoint.builder()
                    .latitude(BigDecimal.valueOf(wpDto.latitude()))
                    .longitude(BigDecimal.valueOf(wpDto.longitude()))
                    .order(wpDto.order())
                    .elevation(wpDto.elevation()) // Dodaj elevation ako postoji
                    .build();
            trail.addWaypoint(wp);
        });

        // NOVO: Izračunaj elevation statistike prije spremanja
        trail.calculateElevationStats();

        Trail saved = trailRepo.save(trail);

        // Map Entity → Response DTO
        return mapToTrailResponseDto(saved);
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
                .orElseThrow(() -> new RuntimeException("Trail not found"));
        return mapToTrailResponseDto(trail);
    }

    private TrailResponseDto mapToTrailResponseDto(Trail trail) {
        var wpDtos = trail.getWaypoints().stream()
                .sorted(Comparator.comparingInt(TrailWaypoint::getOrder)) // Sort by order
                .map(w -> new WaypointDto(
                        w.getLatitude().doubleValue(),
                        w.getLongitude().doubleValue(),
                        w.getOrder(),
                        w.getElevation() // Uključi elevation u response
                ))
                .toList();

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
                wpDtos
        );
    }
}