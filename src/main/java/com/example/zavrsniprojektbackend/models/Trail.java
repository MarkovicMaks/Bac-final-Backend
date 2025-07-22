package com.example.zavrsniprojektbackend.models;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trails")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Trail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "length_km", precision = 8, scale = 2)
    private BigDecimal lengthKm;

    @Column(name = "height_km", precision = 8, scale = 2)
    private BigDecimal heightKm;

    private String difficulty;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "trail",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private List<TrailWaypoint> waypoints = new ArrayList<>();

    public void addWaypoint(TrailWaypoint wp) {
        wp.setTrail(this);
        waypoints.add(wp);
    }
}
