package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * <p>Role class.</p>
 *
 * @author Keitho_
 */
@Entity
@Table(name = "rol")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer roleId;

    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "descripcion", length = 255)
    private String description;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private boolean activo = true;
}
