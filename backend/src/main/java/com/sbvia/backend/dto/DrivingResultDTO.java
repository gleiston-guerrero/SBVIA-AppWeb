package com.sbvia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>DrivingResultDTO class.</p>
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
