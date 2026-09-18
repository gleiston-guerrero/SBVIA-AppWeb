package com.sbvia.backend.repository;

import com.sbvia.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Role.
 *
 * @author Keitho_
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Busca un role por name.
     *
     * @param name a {@link java.lang.String} object
     * @return a {@link java.util.Optional} object
     */
    Optional<Role> findByName(String name);
}
