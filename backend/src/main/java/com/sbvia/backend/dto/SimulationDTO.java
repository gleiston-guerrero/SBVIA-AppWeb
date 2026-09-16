package com.sbvia.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationDTO {

    private Integer simulationId;
    private LocalDate fechaInicio;
    private LocalDate endDate;
    private BigDecimal finalScore;
    private boolean completed;
    private Integer scenarioId;
    private String nombreEscenario;
    private Integer userId;
    private String username;
    private String correoUsuario;
}
