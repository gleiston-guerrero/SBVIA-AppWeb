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
@Table(name = "simulation")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer simulationId;

    @Column(name = "started_at")
    private java.time.LocalDate startDate;

    @Column(name = "ended_at")
    private java.time.LocalDate endDate;

    @Column(name = "final_score", precision = 5, scale = 2)
    private BigDecimal finalScore;

    @Column(name = "attempt_number")
    @Builder.Default
    private Integer numeroIntento = 1;

    @Column(name = "progress_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal porcentajeProgreso = BigDecimal.ZERO;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "completed", nullable = false)
    @Builder.Default
    private boolean completed = false;

    @Column(name = "notes", length = 1000)
    private String observations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_status_id")
    private SimulationState simulationState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_session_id")
    private TrainingSession trainingSession;
}
