package com.sbvia.backend.service.feedback;

import java.math.BigDecimal;

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
