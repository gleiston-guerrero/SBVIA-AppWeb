package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * <p>Scenario class.</p>
 *
 * @author Keitho_
 */
@Entity
@Table(name = "escenario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scenario {
    /** Default constructor for Scenario. */
    public Scenario() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_escenario")
    private Integer scenarioId;

    @Column(name = "nombre", nullable = false, unique = true, length = 255)
    private String name;

    @Column(name = "descripcion", length = 500)
    private String description;

    @Column(name = "longitud_km", precision = 5, scale = 2)
    private java.math.BigDecimal lengthKm;

    @Column(name = "tiempo_estimado_minutos")
    private Integer estimatedTimeMinutes;

    @Column(name = "densidad_trafico", nullable = false, length = 20)
    @Builder.Default
    private String trafficDensity = "MEDIA";

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_via", nullable = false)
    private RoadType roadType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_nivel_dificultad", nullable = false)
    private DifficultyLevel difficultyLevel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_clima", nullable = false)
    private WeatherType weatherType;
}
