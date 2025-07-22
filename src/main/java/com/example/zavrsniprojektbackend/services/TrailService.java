package com.example.zavrsniprojektbackend.services;


import com.example.zavrsniprojektbackend.dtos.CreateTrailRequest;
import com.example.zavrsniprojektbackend.dtos.TrailResponseDto;

public interface TrailService {
    TrailResponseDto createTrail(CreateTrailRequest request);
}
