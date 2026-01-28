package com.example.zavrsniprojektbackend.controllers;

import com.example.zavrsniprojektbackend.dtos.CreateTrailRequest;
import com.example.zavrsniprojektbackend.dtos.TrailResponseDto;
import com.example.zavrsniprojektbackend.models.User;
import com.example.zavrsniprojektbackend.services.TrailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trails")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TrailController {

    private final TrailService trailService;

    @PostMapping
    public TrailResponseDto createTrail(
            @RequestBody CreateTrailRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return trailService.createTrail(request, user);
    }

    @GetMapping
    public ResponseEntity<List<TrailResponseDto>> getAllTrails() {
        List<TrailResponseDto> trails = trailService.getAllTrails();
        return ResponseEntity.ok(trails);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrailResponseDto> getTrailById(@PathVariable Integer id) {
        TrailResponseDto trail = trailService.getTrailById(id);
        return ResponseEntity.ok(trail);
    }

    @GetMapping("/my-trails")
    public ResponseEntity<List<TrailResponseDto>> getMyTrails(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<TrailResponseDto> trails = trailService.getTrailsByUser(user.getId());
        return ResponseEntity.ok(trails);
    }
}