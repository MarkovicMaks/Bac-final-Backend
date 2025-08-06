package com.example.zavrsniprojektbackend.services;


import com.example.zavrsniprojektbackend.dtos.CreateTrailRequest;
import com.example.zavrsniprojektbackend.dtos.TrailResponseDto;

import java.util.List;

public interface TrailService {
    TrailResponseDto createTrail(CreateTrailRequest request);
    List<TrailResponseDto> getAllTrails();
    TrailResponseDto getTrailById(Integer id);
}
