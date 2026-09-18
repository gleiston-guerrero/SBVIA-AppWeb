package com.sbvia.backend.repository;

import com.sbvia.backend.entity.RoadType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * <p>RoadTypeRepository interface.</p>
 *
 * @author Keitho_
 */
@Repository
public interface RoadTypeRepository extends JpaRepository<RoadType, Integer> {

    /**
     * <p>findByName.</p>
     *
     * @param name a {@link java.lang.String} object
     * @return a {@link java.util.Optional} object
     */
    Optional<RoadType> findByName(String name);
}
