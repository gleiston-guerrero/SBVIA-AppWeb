package com.sbvia.backend.repository;

import com.sbvia.backend.entity.Scenario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Scenario} entities.
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

    /**
     * Calls sp_validar_escenario (RF-07): checks that the scenario has at least
     * two traffic rules configured, returning true when it is valid.
     *
     * @param scenarioId the identifier of the scenario to validate
     * @return true when the scenario has the minimum required rules
     */
    @Query(value = "SELECT sp_validar_escenario(:scenarioId)", nativeQuery = true)
    Boolean validateScenario(@Param("scenarioId") Integer scenarioId);
}
