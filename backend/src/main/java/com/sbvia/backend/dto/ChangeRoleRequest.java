package com.sbvia.backend.dto;

import lombok.Data;

/**
 * Request body for changing a user's role.
 *
 * @author Keitho_
 */
@Data
public class ChangeRoleRequest {
    private String nombreRol;
}
