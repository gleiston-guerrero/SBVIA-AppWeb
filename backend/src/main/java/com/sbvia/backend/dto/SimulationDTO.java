package com.sbvia.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * <p>SimulationDTO class.</p>
 *
 * @author Keitho_
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationDTO {

    private Integer simulationId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal finalScore;
    private boolean completed;
    private Integer scenarioId;
    private String scenarioName;
    private Integer userId;
    private String username;
    private String userEmail;
}
