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
     * Invoca sp_calcular_promedio_usuario (RF-04): calcula el promedio de todas
     * las simulaciones completadas de un conductor mediante la funcion PostgreSQL.
     *
     * @param userId el identificador del conductor
     * @return el promedio calculado por el procedimiento almacenado
     */
    @Query(value = "SELECT sp_calcular_promedio_usuario(:userId)", nativeQuery = true)
    BigDecimal calcularPromedioUsuario(@Param("userId") Integer userId);

    /**
     * Invoca sp_reporte_simulacion (RF-03): recupera el detalle multi-tabla
     * de una simulacion mediante la funcion PostgreSQL.
     *
     * @param simulationId el identificador de la simulacion
     * @return lista de filas del reporte [{simulacion_id, usuario_nombre, escenario_nombre, puntaje_final, estado, tiempo_reaccion}]
     */
    @Query(value = "SELECT * FROM sp_reporte_simulacion(:simulationId)", nativeQuery = true)
    List<Object[]> reporteSimulacion(@Param("simulationId") Integer simulationId);

    /**
     * Invoca sp_reporte_actividad_diaria (RF-05): resume la actividad del sistema
     * para la fecha especificada.
     *
     * @param fecha la fecha a consultar en formato ISO (yyyy-MM-dd)
     * @return lista con [total_simulaciones, promedio_puntaje]
     */
    @Query(value = "SELECT * FROM sp_reporte_actividad_diaria(CAST(:fecha AS date))", nativeQuery = true)
    List<Object[]> reporteActividadDiaria(@Param("fecha") String fecha);

    /**
     * Invoca sp_generar_codigo_certificado (RF-08): genera un codigo secuencial
     * unico para el certificado de la simulacion indicada.
     *
     * @param simulationId el identificador de la simulacion aprobada
     * @return el codigo de certificado generado
     */
    @Query(value = "SELECT sp_generar_codigo_certificado(:simulationId)", nativeQuery = true)
    String generarCodigoCertificado(@Param("simulationId") Integer simulationId);

    /**
     * Invoca sp_calcular_puntaje_simulacion (RF-11): recalcula el puntaje
     * de una simulacion restando las penalizaciones de infracciones al puntaje base (100).
     *
     * @param simulationId el identificador de la simulacion
     * @return el puntaje final calculado
     */
    @org.springframework.data.jpa.repository.query.Procedure(procedureName = "sp_calcular_puntaje_simulacion")
    BigDecimal calcularPuntajeSimulacion(@Param("p_id_simulacion") Integer simulationId);
}
