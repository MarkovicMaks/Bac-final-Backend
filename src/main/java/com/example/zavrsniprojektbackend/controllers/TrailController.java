package com.example.zavrsniprojektbackend.controllers;

import com.example.zavrsniprojektbackend.dtos.CreateTrailRequest;
import com.example.zavrsniprojektbackend.dtos.TrailResponseDto;
import com.example.zavrsniprojektbackend.services.TrailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}

