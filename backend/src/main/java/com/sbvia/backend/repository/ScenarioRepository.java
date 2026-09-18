package com.sbvia.backend.repository;

import com.sbvia.backend.entity.Scenario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Invoca sp_validar_escenario (RF-07): valida que el escenario tiene al menos
     * 2 reglas de transito configuradas, devolviendo true si es valido.
     *
     * @param scenarioId el identificador del escenario a validar
     * @return true si el escenario tiene las reglas minimas requeridas
     */
    @Query(value = "SELECT sp_validar_escenario(:scenarioId)", nativeQuery = true)
    Boolean validarEscenario(@Param("scenarioId") Integer scenarioId);
}
