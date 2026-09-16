package com.sbvia.backend.repository;

import com.sbvia.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Role.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Busca un role por name.
     */
    Optional<Role> findByName(String name);
}
