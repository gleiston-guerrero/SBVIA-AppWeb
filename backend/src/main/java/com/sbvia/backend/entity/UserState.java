package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity that defines the lifecycle state of a user account.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "user_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idEstadoUsuario;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "allows_access", nullable = false)
    @Builder.Default
    private boolean permiteAcceso = true;
}
