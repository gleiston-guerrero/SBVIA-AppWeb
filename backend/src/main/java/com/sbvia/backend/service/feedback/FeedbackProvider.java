package com.sbvia.backend.service.feedback;

import com.sbvia.backend.dto.FeedbackIaResponse;

public interface ProveedorFeedback {

    FeedbackIaResponse generar(DatosConduccion datos);

    String origen();
}
