package com.sbvia.backend.repository;

import com.sbvia.backend.entity.WeatherType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link WeatherType} entities.
 *
 * @author Keitho_
 */
@Repository
public interface WeatherTypeRepository extends JpaRepository<WeatherType, Integer> {

    /**
     * <p>findByName.</p>
     *
     * @param name a {@link java.lang.String} object
     * @return a {@link java.util.Optional} object
     */
    Optional<WeatherType> findByName(String name);
}
