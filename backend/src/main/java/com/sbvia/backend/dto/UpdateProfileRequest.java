package com.sbvia.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * <p>UpdateProfileRequest class.</p>
 *
 * @author Keitho_
 */
@Data
public class UpdateProfileRequest {

    @NotBlank(message = "Los firstName son obligatorios")
    @Size(max = 255, message = "Los firstName no pueden exceder los 255 caracteres")
    private String firstName;

    @NotBlank(message = "Los lastName son obligatorios")
    @Size(max = 255, message = "Los lastName no pueden exceder los 255 caracteres")
    private String lastName;

    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    private String phone;
}
