package com.example.zavrsniprojektbackend.controllers;

import com.example.zavrsniprojektbackend.dtos.CreateTrailRequest;
import com.example.zavrsniprojektbackend.dtos.TrailResponseDto;
import com.example.zavrsniprojektbackend.services.TrailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trails")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")   // adjust to your React dev port
public class TrailController {

    private final TrailService trailService;

    @PostMapping
    public TrailResponseDto createTrail(@RequestBody CreateTrailRequest request) {
        return trailService.createTrail(request);
    }

    // Dodaj u TrailController.java
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
}

