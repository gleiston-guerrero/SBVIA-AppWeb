package com.sbvia.backend.repository;

import com.sbvia.backend.entity.TrafficRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link TrafficRule} entities.
 *
 * @author Keitho_
 */
@Repository
public interface TrafficRuleRepository extends JpaRepository<TrafficRule, Integer> {

    /**
     * <p>findByActivaTrue.</p>
     *
     * @return a {@link java.util.List} object
     */
    List<TrafficRule> findByActivaTrue();

    /**
     * <p>findByCodigo.</p>
     *
     * @param codigo a {@link java.lang.String} object
     * @return a {@link java.util.Optional} object
     */
    Optional<TrafficRule> findByCodigo(String codigo);
}
