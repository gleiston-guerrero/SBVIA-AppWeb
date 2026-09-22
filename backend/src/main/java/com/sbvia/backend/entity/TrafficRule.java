package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity that defines a traffic rule enforced during the simulation.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "traffic_rule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idReglaTransito;

    @Column(name = "code", nullable = false, length = 50)
    private String codigo;

    @Column(name = "name", nullable = false, length = 255)
    private String nombre;

    @Column(name = "description", length = 500)
    private String descripcion;

    @Column(name = "category", nullable = false, length = 100)
    private String categoria;

    @Column(name = "base_penalty", nullable = false, precision = 5, scale = 2)
    private java.math.BigDecimal penalizacionBase;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean activa = true;
}
