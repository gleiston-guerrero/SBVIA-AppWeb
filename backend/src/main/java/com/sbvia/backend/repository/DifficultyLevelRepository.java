package com.sbvia.backend.repository;

import com.sbvia.backend.entity.DifficultyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * <p>DifficultyLevelRepository interface.</p>
 *
 * @author Keitho_
 */
@Repository
public interface DifficultyLevelRepository extends JpaRepository<DifficultyLevel, Integer> {

    /**
     * <p>findByName.</p>
     *
     * @param name a {@link java.lang.String} object
     * @return a {@link java.util.Optional} object
     */
    Optional<DifficultyLevel> findByName(String name);
}
