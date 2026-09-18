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
@NoArgsConstructor
@AllArgsConstructor
public class DrivingResultDTO {
    /** Default constructor for DrivingResultDTO. */
    public DrivingResultDTO() {}

    private SimulationDTO simulation;
    private FeedbackIaResponse feedback;
}
