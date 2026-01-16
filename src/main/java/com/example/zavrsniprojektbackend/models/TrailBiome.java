package com.example.zavrsniprojektbackend.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "trail_biomes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TrailBiome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trail_id", nullable = false)
    private Trail trail;

    @Column(name = "zimzelena_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal zimzelenaPercentage = BigDecimal.ZERO;

    @Column(name = "listopadna_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal listopadnaPercentage = BigDecimal.ZERO;

    @Column(name = "livade_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal livadePercentage = BigDecimal.ZERO;

    @Column(name = "urbano_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal urbanoPercentage = BigDecimal.ZERO;

    @Column(name = "polja_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal poljaPercentage = BigDecimal.ZERO;

    @Column(name = "vode_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal vodePercentage = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "analyzed_at")
    private Instant analyzedAt;

    public String getDominantBiome() {
        BigDecimal max = BigDecimal.ZERO;
        String dominant = "nepoznato";

        if (zimzelenaPercentage.compareTo(max) > 0) {
            max = zimzelenaPercentage;
            dominant = "zimzelena";
        }
        if (listopadnaPercentage.compareTo(max) > 0) {
            max = listopadnaPercentage;
            dominant = "listopadna";
        }
        if (livadePercentage.compareTo(max) > 0) {
            max = livadePercentage;
            dominant = "livade";
        }
        if (urbanoPercentage.compareTo(max) > 0) {
            max = urbanoPercentage;
            dominant = "urbano";
        }
        if (poljaPercentage.compareTo(max) > 0) {
            max = poljaPercentage;
            dominant = "polja";
        }
        if (vodePercentage.compareTo(max) > 0) {
            dominant = "vode";
        }

        return dominant;
    }
}