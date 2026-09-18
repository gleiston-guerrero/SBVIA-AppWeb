package com.sbvia.backend.service.feedback;

import java.math.BigDecimal;

/**
 * <p>DrivingData class.</p>
 *
 * @author Keitho_
  * @param durationSeconds durationSeconds param
  * @param averageSpeed averageSpeed param
 */
public record DrivingData(
        int durationSeconds,
        BigDecimal averageSpeed,
        BigDecimal maxSpeed,
        int speedingIncidents,
        int collisions,
        int laneDepartures,
        int ignoredRedLights,
        int respectedRedLights,
        int unsafeDistanceIncidents,
        BigDecimal score,
        String scenarioName,
        int previousPractices,
        BigDecimal previousAverage,
        BigDecimal previousBest) {
}
