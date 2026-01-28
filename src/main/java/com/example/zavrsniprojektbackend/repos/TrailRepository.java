package com.example.zavrsniprojektbackend.repos;

import com.example.zavrsniprojektbackend.models.Trail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrailRepository extends JpaRepository<Trail, Integer> {
    List<Trail> findByCreatedById(Integer userId);
}