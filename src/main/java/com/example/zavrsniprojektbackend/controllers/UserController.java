package com.example.zavrsniprojektbackend.controllers;

import com.example.zavrsniprojektbackend.dtos.UpdateUserRequest;
import com.example.zavrsniprojektbackend.dtos.UserInfoDto;
import com.example.zavrsniprojektbackend.models.User;
import com.example.zavrsniprojektbackend.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserInfoDto> getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(mapToUserInfoDto(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserInfoDto> updateCurrentUser(
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        User updated = userService.updateUser(user.getId(), request);
        return ResponseEntity.ok(mapToUserInfoDto(updated));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
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