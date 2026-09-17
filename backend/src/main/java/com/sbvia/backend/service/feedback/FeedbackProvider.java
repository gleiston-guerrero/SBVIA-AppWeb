package com.sbvia.backend.service.feedback;

import com.sbvia.backend.dto.FeedbackIaResponse;

public interface FeedbackProvider {

    FeedbackIaResponse generate(DrivingData data);

    String origin();
}
