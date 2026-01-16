package com.example.zavrsniprojektbackend.controllers;

import com.example.zavrsniprojektbackend.dtos.RateTrailRequest;
import com.example.zavrsniprojektbackend.dtos.RatingStatsDto;
import com.example.zavrsniprojektbackend.models.Trail;
import com.example.zavrsniprojektbackend.models.TrailRating;
import com.example.zavrsniprojektbackend.models.User;
import com.example.zavrsniprojektbackend.repos.TrailRatingRepository;
import com.example.zavrsniprojektbackend.repos.TrailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trails/{trailId}/rating")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TrailRatingController {

    private final TrailRatingRepository ratingRepo;
    private final TrailRepository trailRepo;

    @PostMapping
    public ResponseEntity<RatingStatsDto> rateTrail(
            @PathVariable Integer trailId,
            @RequestBody RateTrailRequest request,
            Authentication authentication) {

        if (request.rating() < 1 || request.rating() > 5) {
            return ResponseEntity.badRequest().build();
        }

        User user = (User) authentication.getPrincipal();
        Trail trail = trailRepo.findById(trailId)
                .orElseThrow(() -> new RuntimeException("Trail not found"));

        // Check if user already rated this trail
        TrailRating rating = ratingRepo.findByTrailIdAndUserId(trailId, user.getId())
                .orElse(TrailRating.builder()
                        .trail(trail)
                        .user(user)
                        .build());

        rating.setRating(request.rating());
        ratingRepo.save(rating);

        // Return updated stats
        Double avgRating = ratingRepo.getAverageRatingForTrail(trailId);
        Long totalRatings = ratingRepo.getCountForTrail(trailId);

        return ResponseEntity.ok(new RatingStatsDto(avgRating, totalRatings, request.rating()));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteRating(
            @PathVariable Integer trailId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        ratingRepo.deleteByTrailIdAndUserId(trailId, user.getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<RatingStatsDto> getRatingStats(
            @PathVariable Integer trailId,
            Authentication authentication) {

        User user = authentication != null ? (User) authentication.getPrincipal() : null;

        Double avgRating = ratingRepo.getAverageRatingForTrail(trailId);
        Long totalRatings = ratingRepo.getCountForTrail(trailId);

        Integer userRating = null;
        if (user != null) {
            userRating = ratingRepo.findByTrailIdAndUserId(trailId, user.getId())
                    .map(TrailRating::getRating)
                    .orElse(null);
        }

        return ResponseEntity.ok(new RatingStatsDto(avgRating, totalRatings, userRating));
    }
}