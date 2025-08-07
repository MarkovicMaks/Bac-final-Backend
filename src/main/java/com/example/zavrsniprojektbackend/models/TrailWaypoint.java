package com.example.zavrsniprojektbackend.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "trail_waypoints")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TrailWaypoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trail_id", nullable = false)
    private Trail trail;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "order_no", nullable = false)
    private Integer order;

    @Column(precision = 8, scale = 2)
    private BigDecimal elevation; // Novo polje za visinu

    // Helper method za kreiranje waypointa
    public static TrailWaypoint of(BigDecimal latitude, BigDecimal longitude, Integer order, BigDecimal elevation) {
        return TrailWaypoint.builder()
                .latitude(latitude)
                .longitude(longitude)
                .order(order)
                .elevation(elevation)
                .build();
    }
}