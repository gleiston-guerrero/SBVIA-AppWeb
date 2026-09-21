package com.sbvia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Transport object that exposes a driving scenario to the client.
 *
 * @author Keitho_
 */
@Data
@Builder
@AllArgsConstructor
@lombok.NoArgsConstructor
public class ScenarioDTO {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal lengthKm;
    private Integer estimatedTimeMinutes;
    private String trafficDensity;
    private String roadType;
    private String difficultyLevel;
    private String weatherType;
    private boolean activo;
}
