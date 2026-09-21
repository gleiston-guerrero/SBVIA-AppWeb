package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity that defines the lifecycle state of a user account.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "estado_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_usuario")
    private Integer idEstadoUsuario;

    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "descripcion", length = 255)
    private String description;

    @Column(name = "permite_acceso", nullable = false)
    @Builder.Default
    private boolean permiteAcceso = true;
}
