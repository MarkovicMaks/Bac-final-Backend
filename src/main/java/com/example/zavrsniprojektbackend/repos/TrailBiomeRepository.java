// TrailBiomeRepository.java
package com.example.zavrsniprojektbackend.repos;

import com.example.zavrsniprojektbackend.models.TrailBiome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrailBiomeRepository extends JpaRepository<TrailBiome, Integer> {

    @Query("SELECT tb FROM TrailBiome tb WHERE tb.trail.id = :trailId")
    Optional<TrailBiome> findByTrailId(@Param("trailId") Integer trailId);

    void deleteByTrailId(Integer trailId);
}