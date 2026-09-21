package com.sbvia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Transport object that exposes a user account without its credentials.
 *
 * @author Keitho_
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Integer id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String role;
    private String phone;
    private boolean accountLocked;
}
