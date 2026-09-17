package com.sbvia.backend.service.feedback;

import java.math.BigDecimal;

public record DatosConduccion(
        int durationSeconds,
        BigDecimal velocidadPromedio,
        BigDecimal velocidadMaxima,
        int excesosVelocidad,
        int colisiones,
        int salidasCarril,
        int semaforosIgnorados,
        int semaforosRespetados,
        int distanciaInsegura,
        BigDecimal puntaje,
        String nombreEscenario,
        int practicasPrevias,
        BigDecimal promedioPrevio,
        BigDecimal mejorPrevio) {
}
