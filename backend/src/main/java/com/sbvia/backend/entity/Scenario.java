package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * JPA entity that defines a driving scenario available for practice.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "scenario")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer scenarioId;

    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "length_km", precision = 5, scale = 2)
    private java.math.BigDecimal lengthKm;

    @Column(name = "estimated_minutes")
    private Integer estimatedTimeMinutes;

    @Column(name = "traffic_density", nullable = false, length = 20)
    @Builder.Default
    private String trafficDensity = "MEDIA";

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "road_type_id", nullable = false)
    private RoadType roadType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "difficulty_level_id", nullable = false)
    private DifficultyLevel difficultyLevel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "weather_type_id", nullable = false)
    private WeatherType weatherType;
}
