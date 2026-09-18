package com.sbvia.backend.repository;

import com.sbvia.backend.entity.Scenario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p>ScenarioRepository interface.</p>
 *
 * @author Keitho_
 */
@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Integer> {

    /**
     * <p>findByActivoTrue.</p>
     *
     * @param pageable a {@link org.springframework.data.domain.Pageable} object
     * @return a {@link org.springframework.data.domain.Page} object
     */
    Page<Scenario> findByActivoTrue(Pageable pageable);
}
