package com.example.zavrsniprojektbackend.services;

import com.example.zavrsniprojektbackend.dtos.CreateTrailRequest;
import com.example.zavrsniprojektbackend.dtos.TrailResponseDto;
import com.example.zavrsniprojektbackend.models.User;

import java.util.List;

public interface TrailService {
    TrailResponseDto createTrail(CreateTrailRequest request, User user);
    List<TrailResponseDto> getAllTrails();
    TrailResponseDto getTrailById(Integer id);
    List<TrailResponseDto> getTrailsByUser(Integer userId);
}