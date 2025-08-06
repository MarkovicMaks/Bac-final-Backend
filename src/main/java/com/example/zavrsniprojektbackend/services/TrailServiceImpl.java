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

import java.util.List;

@Service @RequiredArgsConstructor
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
                    .latitude(wpDto.latitude())
                    .longitude(wpDto.longitude())
                    .order(wpDto.order())
                    .build();
            trail.addWaypoint(wp);
        });

        Trail saved = trailRepo.save(trail);

        // Map Entity → Response DTO
        var wpDtos = saved.getWaypoints().stream()
                .map(w -> new WaypointDto(w.getLatitude(), w.getLongitude(), w.getOrder()))
                .toList();

        return new TrailResponseDto(
                saved.getId(),
                saved.getName(),
                saved.getLengthKm(),
                saved.getHeightKm(),
                saved.getDifficulty(),
                saved.getCreatedAt(),
                wpDtos
        );
    }
    // TrailServiceImpl.java - implementiraj metode
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
                .sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder())) // Sort by order
                .map(w -> new WaypointDto(w.getLatitude(), w.getLongitude(), w.getOrder()))
                .toList();

        return new TrailResponseDto(
                trail.getId(),
                trail.getName(),
                trail.getLengthKm(),
                trail.getHeightKm(),
                trail.getDifficulty(),
                trail.getCreatedAt(),
                wpDtos
        );
    }
}

