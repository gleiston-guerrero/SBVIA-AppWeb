package com.sbvia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of a driving simulation, with its score and recorded infractions.
 *
 * @author Keitho_
 */
@Data
@Builder
@AllArgsConstructor
@lombok.NoArgsConstructor
public class DrivingResultDTO {
    private SimulationDTO simulation;
    private FeedbackIaResponse feedback;
}
