package com.example.zavrsniprojektbackend.repos;

import com.example.zavrsniprojektbackend.models.TrailRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrailRatingRepository extends JpaRepository<TrailRating, Integer> {

    @Query("SELECT AVG(r.rating) FROM TrailRating r WHERE r.trail.id = :trailId")
    Double getAverageRatingForTrail(@Param("trailId") Integer trailId);

    @Query("SELECT COUNT(r) FROM TrailRating r WHERE r.trail.id = :trailId")
    Long getCountForTrail(@Param("trailId") Integer trailId);

    Optional<TrailRating> findByTrailIdAndUserId(Integer trailId, Integer userId);

    void deleteByTrailIdAndUserId(Integer trailId, Integer userId);
}