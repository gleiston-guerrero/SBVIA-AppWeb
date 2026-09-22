package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity that classifies the road events that can be recorded.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "event_type")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class EventType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idTipoEvento;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "category", length = 100)
    private String categoria;
}
