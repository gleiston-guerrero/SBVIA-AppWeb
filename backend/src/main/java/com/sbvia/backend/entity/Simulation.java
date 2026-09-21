package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * JPA entity that represents a practice session carried out by a user.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "simulacion")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_simulacion")
    private Integer simulationId;

    @Column(name = "fecha_inicio")
    private java.time.LocalDate startDate;

    @Column(name = "fecha_fin")
    private java.time.LocalDate endDate;

    @Column(name = "puntaje_final", precision = 5, scale = 2)
    private BigDecimal finalScore;

    @Column(name = "numero_intento")
    @Builder.Default
    private Integer numeroIntento = 1;

    @Column(name = "porcentaje_progreso", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal porcentajeProgreso = BigDecimal.ZERO;

    @Column(name = "duracion_segundos")
    private Integer durationSeconds;

    @Column(name = "completada", nullable = false)
    @Builder.Default
    private boolean completed = false;

    @Column(name = "observaciones", length = 1000)
    private String observations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_escenario", nullable = false)
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_simulacion")
    private SimulationState simulationState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sesion")
    private TrainingSession trainingSession;
}
