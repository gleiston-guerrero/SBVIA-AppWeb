package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA entity that tracks how far a simulation has advanced.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "simulation_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulationProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idProgreso;

    @Column(name = "percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje;

    @Column(name = "stage", length = 100)
    private String etapa;

    @Column(name = "position_x")
    private BigDecimal posicionX;

    @Column(name = "position_y")
    private BigDecimal posicionY;

    @Column(name = "current_speed_kmh")
    private BigDecimal velocidadActualKmh;

    @Column(name = "occurred_at", nullable = false)
    @Builder.Default
    private Instant fechaHora = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;
}
