package com.example.zavrsniprojektbackend.dtos;

public record UserInfoDto(
        Integer id,
        String fullName,
        String email,
        String role
) {}