package com.sbvia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>FeedbackIaResponse class.</p>
 *
 * @author Keitho_
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackIaResponse {
    /** Default constructor for FeedbackIaResponse. */
    public FeedbackIaResponse() {}

    private String resumen;
    private List<String> aciertos;
    private List<String> errores;
    private String nivelRiesgo;
    private List<String> recomendaciones;
    private BigDecimal puntaje;
    private String mensajeMotivador;
    private String comparacion;
    private String origen;
}
