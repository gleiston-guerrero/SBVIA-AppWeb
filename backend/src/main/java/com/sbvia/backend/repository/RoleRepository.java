package com.sbvia.backend.repository;

import com.sbvia.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for the Role entity.
 *
 * @author Keitho_
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Looks up a role by name.
     *
     * @param name a {@link java.lang.String} object
     * @return a {@link java.util.Optional} object
     */
    Optional<Role> findByName(String name);
}
