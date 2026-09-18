package com.sbvia.backend.service.feedback;

import com.sbvia.backend.dto.FeedbackIaResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>LocalFeedbackService class.</p>
 *
 * @author Keitho_
 */
@Service
public class LocalFeedbackService implements FeedbackProvider {
    /** Default constructor for LocalFeedbackService. */
    public LocalFeedbackService() {}

    /** Constant <code>ORIGIN="IA_LOCAL"</code> */
    public static final String ORIGIN = "IA_LOCAL";

    @Override
    /**
     * {@inheritDoc}
     *
     * Método público.
     */
    public String origin() {
        return ORIGIN;
    }

    @Override
    /**
     * {@inheritDoc}
     *
     * Método público.
     */
    public FeedbackIaResponse generate(DrivingData d) {
        int total = d.speedingIncidents() + d.collisions() + d.laneDepartures()
                + d.ignoredRedLights() + d.unsafeDistanceIncidents();
        String risk = riskLevel(d);

        List<String> hits = new ArrayList<>();
        if (d.collisions() == 0) hits.add("Condujo sin colisiones");
        if (d.speedingIncidents() == 0) hits.add("Respetó los límites de velocidad");
        if (d.laneDepartures() == 0) hits.add("Mantuvo el vehículo dentro del carril");
        if (d.ignoredRedLights() == 0) hits.add("Respetó la señalización de los semáforos");
        if (d.unsafeDistanceIncidents() == 0) hits.add("Conservó una distancia segura");
        if (d.respectedRedLights() > 0) {
            hits.add("Se detuvo correctamente ante " + d.respectedRedLights() + " semáforo(s) en rojo");
        }
        if (hits.isEmpty()) hits.add("Completó el recorrido del scenario");

        List<String> mistakes = new ArrayList<>();
        if (d.collisions() > 0) mistakes.add("Colisionó " + d.collisions() + " vez/veces con otros vehículos");
        if (d.speedingIncidents() > 0) mistakes.add("Excedió el límite de velocidad en " + d.speedingIncidents() + " ocasión(es)");
        if (d.laneDepartures() > 0) mistakes.add("Salió del carril " + d.laneDepartures() + " vez/veces");
        if (d.ignoredRedLights() > 0) mistakes.add("Ignoró " + d.ignoredRedLights() + " semáforo(s) en rojo");
        if (d.unsafeDistanceIncidents() > 0) mistakes.add("No conservó la distancia segura en " + d.unsafeDistanceIncidents() + " ocasión(es)");

        return FeedbackIaResponse.builder()
                .resumen(summary(d, total, risk))
                .aciertos(hits)
                .errores(mistakes)
                .nivelRiesgo(risk)
                .recomendaciones(recommendations(d))
                .puntaje(d.score().setScale(2, RoundingMode.HALF_UP))
                .mensajeMotivador(message(risk))
                .comparacion(comparison(d))
                .origen(ORIGIN)
                .build();
    }

    private String riskLevel(DrivingData d) {
        if (d.score().compareTo(new BigDecimal("70")) < 0
                || d.collisions() > 0 || d.ignoredRedLights() > 1) {
            return "ALTO";
        }
        int total = d.speedingIncidents() + d.collisions() + d.laneDepartures()
                + d.ignoredRedLights() + d.unsafeDistanceIncidents();
        if (d.score().compareTo(new BigDecimal("85")) < 0 || total > 0) {
            return "MEDIO";
        }
        return "BAJO";
    }

    private String summary(DrivingData d, int total, String risk) {
        String base = "Recorrido de " + d.durationSeconds() + " segundos en " + d.scenarioName()
                + " con velocidad promedio de " + d.averageSpeed() + " km/h";
        if (total == 0) {
            return base + ". Conducción limpia, sin infractions registradas.";
        }
        String level = "ALTO".equals(risk) ? "varios aspectos críticos"
                : "algunos aspectos por mejorar";
        return base + ". Se registraron " + total + " infracción(es); hay " + level + " antes de la próxima práctica.";
    }

    private List<String> recommendations(DrivingData d) {
        List<String> recs = new ArrayList<>();
        if (d.unsafeDistanceIncidents() > 0 || d.collisions() > 0) {
            recs.add("Aumentar la distancia respecto al vehículo delantero");
        }
        if (d.speedingIncidents() > 0) {
            recs.add("Reducir la velocidad antes de las curvas y zonas señalizadas");
        }
        if (d.ignoredRedLights() > 0) {
            recs.add("Prestar atención anticipada a los semáforos y frenar con tiempo");
        }
        if (d.laneDepartures() > 0) {
            recs.add("Corregir la dirección con movimientos suaves para no salir del carril");
        }
        recs.add("Mirar los espejos cada pocos segundos para anticipar el tráfico");
        recs.add("Practicar arranques y frenadas progresivas para un mejor control");
        recs.add("Planificar la ruta y respetar los límites de cada tramo");
        return recs.subList(0, Math.min(3, recs.size()));
    }

    private String message(String risk) {
        return switch (risk) {
            case "BAJO" -> "Excelente conducción. Sigue así y mantén la concentración.";
            case "MEDIO" -> "Tu conducción es buena, pero todavía puedes mejorar la anticipación.";
            default -> "Cada práctica cuenta: enfócate en una mejora a la vez y verás el progreso.";
        };
    }

    /**
     * Método público.
     *
     * @param d a {@link com.sbvia.backend.service.feedback.DrivingData} object
     * @return a {@link java.lang.String} object
     */
    public String compare(DrivingData d) {
        return comparison(d);
    }

    private String comparison(DrivingData d) {
        if (d.previousPractices() <= 0) {
            return "Es tu primera simulación registrada: este puntaje será tu punto de partida.";
        }
        int cmp = d.score().compareTo(d.previousAverage());
        String trend = cmp > 0 ? "por encima de" : cmp < 0 ? "por debajo de" : "igual a";
        return "Promedio de tus " + d.previousPractices() + " práctica(s) anterior(es): "
                + d.previousAverage() + " puntos (mejor marca: " + d.previousBest()
                + "). Este resultado está " + trend + " tu promedio.";
    }
}
