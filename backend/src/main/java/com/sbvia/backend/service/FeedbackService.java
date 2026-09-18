package com.sbvia.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbvia.backend.dto.FeedbackIaResponse;
import com.sbvia.backend.entity.Infraction;
import com.sbvia.backend.entity.PerformanceMetric;
import com.sbvia.backend.entity.Feedback;
import com.sbvia.backend.entity.Simulation;
import com.sbvia.backend.entity.User;
import com.sbvia.backend.exception.ResourceNotFoundException;
import com.sbvia.backend.repository.InfractionRepository;
import com.sbvia.backend.repository.PerformanceMetricRepository;
import com.sbvia.backend.repository.FeedbackRepository;
import com.sbvia.backend.repository.SimulationRepository;
import com.sbvia.backend.repository.UserRepository;
import com.sbvia.backend.service.feedback.DrivingData;
import com.sbvia.backend.service.feedback.AiUnavailableException;
import com.sbvia.backend.service.feedback.ExternalAiFeedbackService;
import com.sbvia.backend.service.feedback.LocalFeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>FeedbackService class.</p>
 *
 * @author Keitho_
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FeedbackService {
    /** Default constructor for FeedbackService. */
    public FeedbackService() {}

    private final SimulationRepository simulationRepository;
    private final UserRepository userRepository;
    private final PerformanceMetricRepository performanceMetricRepository;
    private final InfractionRepository infractionRepository;
    private final FeedbackRepository feedbackRepository;
    private final LocalFeedbackService localMotor;
    private final ExternalAiFeedbackService externalProvider;
    private final ObjectMapper objectMapper;

    /**
     * Método público.
     *
     * @param email a {@link java.lang.String} object
     * @param simulationId a {@link java.lang.Integer} object
     * @return a {@link com.sbvia.backend.dto.FeedbackIaResponse} object
     */
    public FeedbackIaResponse generateReport(String email, Integer simulationId) {
        DrivingData data = buildData(email, simulationId);
        return generateWithFallback(data);
    }

    /**
     * Método público.
     *
     * @param email a {@link java.lang.String} object
     * @param simulationId a {@link java.lang.Integer} object
     * @return a {@link com.sbvia.backend.dto.FeedbackIaResponse} object
     */
    public FeedbackIaResponse generateAndSave(String email, Integer simulationId) {
        Simulation simulation = loadOwnSimulation(email, simulationId);
        DrivingData data = buildData(simulation);
        FeedbackIaResponse report = generateWithFallback(data);
        feedbackRepository.save(Feedback.builder()
                .comentario(truncate(report.getResumen()))
                .recomendacion(truncate(String.join("; ", report.getRecomendaciones())))
                .origen(report.getOrigen())
                .simulation(simulation)
                .build());
        return report;
    }

    private FeedbackIaResponse generateWithFallback(DrivingData data) {
        if (externalProvider.isEnabled()) {
            try {
                FeedbackIaResponse external = externalProvider.generate(data);
                if (external.getComparacion() == null) {
                    external.setComparacion(localMotor.compare(data));
                }
                return external;
            } catch (AiUnavailableException e) {
                log.warn("Proveedor externo de IA no disponible, se usa el motor local: {} - Causa: {}", 
                         e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "N/A");
            }
        }
        return localMotor.generate(data);
    }

    private Simulation loadOwnSimulation(String email, Integer simulationId) {
        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new ResourceNotFoundException("Simulación no encontrada"));
        if (!simulation.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new AccessDeniedException("La simulación pertenece a otro user");
        }
        return simulation;
    }

    private DrivingData buildData(String email, Integer simulationId) {
        return buildData(loadOwnSimulation(email, simulationId));
    }

    private DrivingData buildData(Simulation simulation) {
        Map<String, BigDecimal> metrics = new java.util.HashMap<>();
        for (PerformanceMetric m : performanceMetricRepository
                .findBySimulation_SimulationId(simulation.getSimulationId())) {
            if (m.getMetricType() != null && m.getValue() != null) {
                metrics.put(m.getMetricType().getName(), m.getValue());
            }
        }
        Map<?, ?> snapshot = readSnapshot(simulation.getObservations());
        int speeding = toInt(snapshot.get("excesos"));
        int collisions = toInt(snapshot.get("colisiones"));
        int laneDepartures = toInt(snapshot.get("salidas"));
        int redLights = toInt(snapshot.get("semaforos"));
        int unsafeDistance = toInt(snapshot.get("distancia"));
        int respectedLights = toInt(snapshot.get("respetados"));

        User user = simulation.getUser();
        List<BigDecimal> previousScores = new ArrayList<>();
        for (Simulation s : simulationRepository
                .findByUser_UserIdOrderBySimulationIdDesc(user.getUserId())) {
            if (s.isCompleted() && !s.getSimulationId().equals(simulation.getSimulationId())
                    && s.getFinalScore() != null) {
                previousScores.add(s.getFinalScore());
            }
        }
        BigDecimal average = previousScores.isEmpty() ? BigDecimal.ZERO
                : previousScores.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(previousScores.size()), 2, RoundingMode.HALF_UP);
        BigDecimal best = previousScores.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

        return new DrivingData(
                simulation.getDurationSeconds() != null ? simulation.getDurationSeconds() : 0,
                metrics.getOrDefault("VELOCIDAD_PROMEDIO", BigDecimal.ZERO),
                toBigDecimal(snapshot.get("velocidadMaxima")),
                speeding, collisions, laneDepartures, redLights, respectedLights, unsafeDistance,
                simulation.getFinalScore() != null ? simulation.getFinalScore() : BigDecimal.ZERO,
                simulation.getScenario() != null ? simulation.getScenario().getName() : "el scenario",
                previousScores.size(), average, best);
    }

    @SuppressWarnings("unchecked")
    private Map<?, ?> readSnapshot(String observations) {
        if (observations == null || !observations.trim().startsWith("{")) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(observations, Map.class);
        } catch (Exception e) {
            log.warn("No se pudo interpretar el snapshot de métricas: {}", e.getMessage());
            return Map.of();
        }
    }

    private int toInt(Object value) {
        if (value instanceof Number n) return n.intValue();
        return 0;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof Number n) return new BigDecimal(n.toString());
        return BigDecimal.ZERO;
    }

    private String truncate(String text) {
        if (text == null) return "";
        return text.length() <= 1000 ? text : text.substring(0, 1000);
    }
}
