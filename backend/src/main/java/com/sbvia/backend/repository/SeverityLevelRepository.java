package com.sbvia.backend.repository;

import com.sbvia.backend.entity.SeverityLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link SeverityLevel} entities.
 *
 * @author Keitho_
 */
@Repository
public interface SeverityLevelRepository extends JpaRepository<SeverityLevel, Integer> {

    /**
     * <p>findByName.</p>
     *
     * @param name a {@link java.lang.String} object
     * @return a {@link java.util.Optional} object
     */
    Optional<SeverityLevel> findByName(String name);
}
