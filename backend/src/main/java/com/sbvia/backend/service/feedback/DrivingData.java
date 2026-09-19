package com.sbvia.backend.service.feedback;

import java.math.BigDecimal;

/**
 * <p>DrivingData class.</p>
 *
 * @author Keitho_
  * @param durationSeconds durationSeconds param
  * @param averageSpeed averageSpeed param
  * @param maxSpeed the maximum speed reached during the driving session, in km/h
  * @param speedingIncidents the number of speeding incidents recorded during the session
  * @param collisions the number of collisions recorded during the session
  * @param laneDepartures the number of lane departures recorded during the session
  * @param ignoredRedLights the number of red lights the driver failed to respect
  * @param respectedRedLights the number of red lights the driver respected
  * @param unsafeDistanceIncidents the number of unsafe-following-distance incidents recorded
  * @param score the overall driving performance score calculated for the session
  * @param scenarioName the name of the driving scenario in which the session took place
  * @param previousPractices the number of practice sessions completed before this one
  * @param previousAverage the average score of previous practice sessions
  * @param previousBest the best score achieved in previous practice sessions
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
