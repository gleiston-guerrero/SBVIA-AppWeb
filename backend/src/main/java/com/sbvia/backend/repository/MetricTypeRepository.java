package com.sbvia.backend.repository;

import com.sbvia.backend.entity.MetricType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link MetricType} entities.
 *
 * @author Keitho_
 */
@Repository
public interface MetricTypeRepository extends JpaRepository<MetricType, Integer> {

    /**
     * <p>findByName.</p>
     *
     * @param name a {@link java.lang.String} object
     * @return a {@link java.util.Optional} object
     */
    Optional<MetricType> findByName(String name);
}
