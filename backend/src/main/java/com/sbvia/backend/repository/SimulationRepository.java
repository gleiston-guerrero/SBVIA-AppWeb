package com.sbvia.backend.repository;

import com.sbvia.backend.entity.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Spring Data JPA repository for {@link Simulation} entities.
 *
 * @author Keitho_
 */
@Repository
public interface SimulationRepository extends JpaRepository<Simulation, Integer> {

    /**
     * <p>findByUser_UserIdOrderBySimulationIdDesc.</p>
     *
     * @param userId a {@link java.lang.Integer} object
     * @return a {@link java.util.List} object
     */
    List<Simulation> findByUser_UserIdOrderBySimulationIdDesc(Integer userId);

    /**
     * <p>findAllByOrderBySimulationIdDesc.</p>
     *
     * @return a {@link java.util.List} object
     */
    List<Simulation> findAllByOrderBySimulationIdDesc();

    /**
     * <p>getGlobalStats.</p>
     *
     * @return an array of {@link java.lang.Object} objects
     */
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(s), AVG(s.finalScore), SUM(CASE WHEN s.finalScore >= 70 THEN 1 ELSE 0 END) FROM Simulation s WHERE s.completed = true")
    Object[] getGlobalStats();

    /**
     * Calls sp_calcular_promedio_usuario (RF-04): computes the average of every
     * completed simulation of a driver, through the PostgreSQL function.
     *
     * @param userId the identifier of the driver
     * @return the average computed by the stored procedure
     */
    @Query(value = "SELECT sp_calcular_promedio_usuario(:userId)", nativeQuery = true)
    BigDecimal calculateUserAverage(@Param("userId") Integer userId);

    /**
     * Calls sp_reporte_simulacion (RF-03): retrieves the multi-table detail
     * of a simulation through the PostgreSQL function.
     *
     * @param simulationId the identifier of the simulation
     * @return lista de filas del reporte [{simulacion_id, usuario_nombre, escenario_nombre, puntaje_final, estado, tiempo_reaccion}]
     */
    @Query(value = "SELECT * FROM sp_reporte_simulacion(:simulationId)", nativeQuery = true)
    List<Object[]> simulationReport(@Param("simulationId") Integer simulationId);

    /**
     * Calls sp_reporte_actividad_diaria (RF-05): summarises system activity
     * for the given date.
     *
     * @param fecha the date to query, in ISO format (yyyy-MM-dd)
     * @return lista con [total_simulaciones, promedio_puntaje]
     */
    @Query(value = "SELECT * FROM sp_reporte_actividad_diaria(CAST(:fecha AS date))", nativeQuery = true)
    List<Object[]> dailyActivityReport(@Param("fecha") String fecha);

    /**
     * Calls sp_generar_codigo_certificado (RF-08): generates a sequential code
     * unique to the certificate of the given simulation.
     *
     * @param simulationId the identifier of the simulation aprobada
     * @return the generated certificate code
     */
    @Query(value = "SELECT sp_generar_codigo_certificado(:simulationId)", nativeQuery = true)
    String generateCertificateCode(@Param("simulationId") Integer simulationId);

    /**
     * Calls sp_calcular_puntaje_simulacion (RF-11): recomputes the score
     * of a simulation, subtracting infraction penalties from the base score of 100.
     *
     * @param simulationId the identifier of the simulation
     * @return the computed final score
     */
    @org.springframework.data.jpa.repository.query.Procedure(procedureName = "sp_calcular_puntaje_simulacion")
    BigDecimal calculateSimulationScore(@Param("p_id_simulacion") Integer simulationId);
}
