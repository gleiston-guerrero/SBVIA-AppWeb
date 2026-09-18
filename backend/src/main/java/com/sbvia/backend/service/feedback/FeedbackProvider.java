package com.sbvia.backend.service.feedback;

import com.sbvia.backend.dto.FeedbackIaResponse;

/**
 * <p>FeedbackProvider interface.</p>
 *
 * @author Keitho_
 */
public interface FeedbackProvider {

    /**
     * <p>generate.</p>
     *
     * @param data a {@link com.sbvia.backend.service.feedback.DrivingData} object
     * @return a {@link com.sbvia.backend.dto.FeedbackIaResponse} object
     */
    FeedbackIaResponse generate(DrivingData data);

    /**
     * <p>origin.</p>
     *
     * @return a {@link java.lang.String} object
     */
    String origin();
}
