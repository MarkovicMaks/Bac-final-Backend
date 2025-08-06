package com.example.zavrsniprojektbackend.services;

import com.example.zavrsniprojektbackend.dtos.*;
import com.example.zavrsniprojektbackend.models.User;
import com.example.zavrsniprojektbackend.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse signUp(SignUpRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }

        // Create new user
        var user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(User.Role.USER)
                .build();

        var savedUser = userRepository.save(user);

        // Generate tokens
        var accessToken = jwtService.generateAccessToken(savedUser);
        var refreshToken = jwtService.generateRefreshToken(savedUser);

        return AuthResponse.of(
                accessToken,
                refreshToken,
                mapToUserInfoDto(savedUser)
        );
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // Find user
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Generate tokens
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.of(
                accessToken,
                refreshToken,
                mapToUserInfoDto(user)
        );
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        final String userEmail = jwtService.extractUsername(request.refreshToken());

        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (jwtService.isTokenValid(request.refreshToken(), user)) {
                var accessToken = jwtService.generateAccessToken(user);
                return AuthResponse.of(
                        accessToken,
                        request.refreshToken(), // Keep same refresh token
                        mapToUserInfoDto(user)
                );
            }
        }
        throw new RuntimeException("Invalid refresh token");
    }

    private UserInfoDto mapToUserInfoDto(User user) {
        return new UserInfoDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}