package com.sbvia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Aggregated practice statistics, either global or for a single user.
 *
 * @author Keitho_
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {
    private long totalPracticas;
    private int promedioGlobal;
    private int tasaAprobacionGlobal;
}
