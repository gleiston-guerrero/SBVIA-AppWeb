package com.sbvia.backend.service.feedback;

import com.sbvia.backend.dto.FeedbackIaResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class FeedbackLocalServiceTest {

    private final LocalFeedbackService motor = new LocalFeedbackService();

    private DrivingData base() {
        return new DrivingData(120, new BigDecimal("45.50"), new BigDecimal("72.00"),
                0, 0, 0, 0, 0, 0, new BigDecimal("100.00"), "Centro urbano", 0,
                BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test
    void cleanDrivingGivesLowRiskAndTopScore() {
        FeedbackIaResponse informe = motor.generate(base());

        assertThat(informe.getNivelRiesgo()).isEqualTo("BAJO");
        assertThat(informe.getPuntaje()).isEqualByComparingTo("100.00");
        assertThat(informe.getErrores()).isEmpty();
        assertThat(informe.getAciertos()).isNotEmpty();
        assertThat(informe.getRecomendaciones()).hasSize(3);
        assertThat(informe.getOrigen()).isEqualTo("IA_LOCAL");
        assertThat(informe.getComparacion()).contains("primera simulación");
    }

    @Test
    void collisionAndTrafficLightGiveHighRisk() {
        DrivingData datos = new DrivingData(90, new BigDecimal("50.00"), new BigDecimal("80.00"),
                1, 1, 0, 1, 0, 0, new BigDecimal("50.00"), "Autopista", 3,
                new BigDecimal("70.00"), new BigDecimal("80.00"));

        FeedbackIaResponse informe = motor.generate(datos);

        assertThat(informe.getNivelRiesgo()).isEqualTo("ALTO");
        assertThat(informe.getErrores()).hasSize(3);
        assertThat(informe.getRecomendaciones()).hasSize(3);
        assertThat(informe.getComparacion()).contains("por debajo de");
    }

    @Test
    void comparesAgainstPreviousAverage() {
        DrivingData datos = new DrivingData(60, new BigDecimal("40.00"), new BigDecimal("55.00"),
                0, 0, 1, 0, 1, 0, new BigDecimal("90.00"), "Zona escolar", 2,
                new BigDecimal("80.00"), new BigDecimal("82.00"));

        assertThat(motor.compare(datos)).contains("por encima de");
        assertThat(motor.generate(datos).getNivelRiesgo()).isEqualTo("MEDIO");
    }
}
