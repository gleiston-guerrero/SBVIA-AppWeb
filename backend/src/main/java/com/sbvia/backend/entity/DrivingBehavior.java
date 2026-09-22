package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA entity that describes a driving behaviour assessed in a simulation.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "road_behavior")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class DrivingBehavior {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idComportamiento;

    @Column(name = "classification", nullable = false, length = 100)
    private String clasificacion;

    @Column(name = "risk_level", nullable = false)
    private Integer nivelRiesgo;

    @Column(name = "safety_score")
    private BigDecimal puntajeSeguridad;

    @Column(name = "responsibility_score")
    private BigDecimal puntajeResponsabilidad;

    @Column(name = "compliance_score")
    private BigDecimal puntajeCumplimiento;

    @Column(name = "notes", length = 500)
    private String observations;

    @Column(name = "evaluated_at", nullable = false)
    @Builder.Default
    private Instant fechaEvaluacion = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;
}
