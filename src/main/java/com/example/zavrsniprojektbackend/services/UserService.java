package com.example.zavrsniprojektbackend.services;

import com.example.zavrsniprojektbackend.dtos.UpdateUserRequest;
import com.example.zavrsniprojektbackend.models.User;
import com.example.zavrsniprojektbackend.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User updateUser(Integer userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update full name if provided
        if (request.fullName() != null && !request.fullName().trim().isEmpty()) {
            user.setFullName(request.fullName().trim());
        }

        // Update email if provided and different
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            // Check if new email is already taken
            if (userRepository.existsByEmail(request.email())) {
                throw new RuntimeException("Email already in use");
            }

            // Verify current password for security
            if (request.currentPassword() == null ||
                    !passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
                throw new RuntimeException("Current password is incorrect");
            }

            user.setEmail(request.email());
        }

        // Update password if provided
        if (request.newPassword() != null && !request.newPassword().trim().isEmpty()) {
            // Verify current password
            if (request.currentPassword() == null ||
                    !passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
                throw new RuntimeException("Current password is incorrect");
            }

            user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}