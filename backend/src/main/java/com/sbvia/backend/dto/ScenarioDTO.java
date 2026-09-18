package com.sbvia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * <p>ScenarioDTO class.</p>
 *
 * @author Keitho_
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioDTO {
    /** Default constructor for ScenarioDTO. */
    public ScenarioDTO() {}

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
