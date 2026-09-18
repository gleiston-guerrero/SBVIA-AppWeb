package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <p>EventType class.</p>
 *
 * @author Keitho_
 */
@Entity
@Table(name = "tipo_evento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventType {
    /** Default constructor for EventType. */
    public EventType() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_evento")
    private Integer idTipoEvento;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "descripcion", length = 255)
    private String description;

    @Column(name = "categoria", length = 100)
    private String categoria;
}
