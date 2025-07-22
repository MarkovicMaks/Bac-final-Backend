package com.example.zavrsniprojektbackend.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trail_waypoints")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TrailWaypoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private double latitude;
    private double longitude;

    @Column(name = "order_no")
    private int order;

    // ───── RELATIONSHIP ─────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trail_id")
    private Trail trail;
}

