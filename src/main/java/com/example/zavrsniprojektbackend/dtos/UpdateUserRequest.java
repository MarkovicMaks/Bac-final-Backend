package com.example.zavrsniprojektbackend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(max = 255, message = "Name too long")
        String fullName,

        @Email(message = "Invalid email format")
        String email,

        @Size(min = 8, message = "Password must be at least 8 characters")
        String newPassword,

        String currentPassword  // Required if changing email or password
) {}