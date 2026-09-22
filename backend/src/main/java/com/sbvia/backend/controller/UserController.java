package com.sbvia.backend.controller;

import com.sbvia.backend.dto.UserDTO;
import com.sbvia.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import com.sbvia.backend.dto.UpdateUserRequest;
import com.sbvia.backend.dto.ChangeRoleRequest;
import jakarta.validation.Valid;

/**
 * REST controller for authenticated-user operations.
 *
 * @author Keitho_
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operaciones del user autenticado")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final AuthService authService;

    /**
     * GET /api/users/me - Return the authenticated user profile.
     *
     * @param authentication the current authentication object holding the user credentials
     * @return an HTTP response containing the UserDTO with the user profile information
     */
    @GetMapping("/me")
    @Operation(summary = "Perfil del user", description = "Devuelve los datos del user autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Datos devueltos exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado o token expirado")
    })
    /**
     * Returns the profile of the currently authenticated user, resolved from
     * the email contained in the authentication token.
     *
     * @param authentication the current authentication object holding the authenticated user's identity
     * @return a {@link org.springframework.http.ResponseEntity} carrying the {@link com.sbvia.backend.dto.UserDTO} with the user's profile data
     */
    public ResponseEntity<UserDTO> getCurrentProfile(Authentication authentication) {
        String email = authentication.getName();
        UserDTO user = authService.getCurrentUser(email);
        return ResponseEntity.ok(user);
    }

    /**
     * PUT /api/users/me - Update the authenticated user profile.
     *
     * @param authentication the current authentication object
     * @param request the data to update in the user profile
     * @return an HTTP response with the updated UserDTO
     */
    @PutMapping("/me")
    @Operation(summary = "Actualizar perfil", description = "Actualiza los datos del user autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado o token expirado")
    })
    /**
     * Updates the profile data of the currently authenticated user, resolved
     * from the email in the authentication token, and returns the updated
     * profile.
     *
     * @param authentication the current authentication object holding the authenticated user's identity
     * @param request the validated profile fields to update (names, email and similar)
     * @return a {@link org.springframework.http.ResponseEntity} carrying the {@link com.sbvia.backend.dto.UserDTO} with the updated profile
     */
    public ResponseEntity<UserDTO> updateCurrentUserProfile(
            Authentication authentication,
            @Valid @RequestBody com.sbvia.backend.dto.UpdateProfileRequest request) {
        String email = authentication.getName();
        UserDTO actualizado = authService.updateCurrentUserProfile(email, request);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * GET /api/users - List every user with pagination (Admin only).
     *
     * @param pageable object holding the requested pagination (page, size)
     * @return a Page of UserDTO objects representing the users of the system
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Operation(summary = "Listar users", description = "Lista todos los users (Solo Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista devuelta exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    /**
     * Lists every user in the system as a page of DTOs. Only users with the
     * ADMINISTRADOR authority may call this endpoint.
     *
     * @param pageable the pagination settings (page number, size and ordering)
     * @return a {@link org.springframework.http.ResponseEntity} carrying a {@link org.springframework.data.domain.Page} of {@link com.sbvia.backend.dto.UserDTO}
     */
    public ResponseEntity<Page<UserDTO>> listUsers(Pageable pageable) {
        return ResponseEntity.ok(authService.listUsers(pageable));
    }

    /**
     * PUT /api/users/{id}/role - Change a user role (Admin only).
     *
     * @param id the unique identifier of the user whose role changes
     * @param request the object holding the name of the new role to assign
     * @return an HTTP response with the UserDTO reflecting the updated role
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Operation(summary = "Cambiar role de user", description = "Asigna un nuevo role a un user (Solo Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role actualizado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    /**
     * Changes the role assigned to the user identified by the given ID and
     * returns the user with the updated role. Only users with the
     * ADMINISTRADOR authority may call this endpoint.
     *
     * @param id the unique identifier of the user whose role is changed
     * @param request the validated request containing the name of the new role to assign
     * @return a {@link org.springframework.http.ResponseEntity} carrying the {@link com.sbvia.backend.dto.UserDTO} with the updated role
     */
    public ResponseEntity<UserDTO> changeRole(
            @PathVariable Integer id,
            @Valid @RequestBody ChangeRoleRequest request) {
        UserDTO actualizado = authService.changeRole(id, request.getNombreRol());
        return ResponseEntity.ok(actualizado);
    }

    /**
     * PUT /api/users/{id} - Update a user's data (Admin only).
     *
     * @param id the unique identifier of the user to update
     * @param request the object with the new data to update (first name, last name, state)
     * @return an HTTP response with the updated UserDTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Operation(summary = "Actualizar datos de user", description = "Modifica los datos de un user (Solo Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User actualizado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    /**
     * Updates the data of the user identified by the given ID and returns the
     * updated user. Only users with the ADMINISTRADOR authority may call this
     * endpoint; a 409 conflict is returned if the new email is already taken.
     *
     * @param id the unique identifier of the user to update
     * @param request the validated request with the new user data (names, email and state)
     * @return a {@link org.springframework.http.ResponseEntity} carrying the {@link com.sbvia.backend.dto.UserDTO} with the updated data
     */
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserDTO actualizado = authService.updateUser(id, request);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * DELETE /api/users/{id} - Deactivate a user (Admin only).
     *
     * @param id the unique identifier of the user to deactivate
     * @return an HTTP 204 response with no content if the operation succeeded
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Operation(summary = "Eliminar (desactivar) user", description = "Soft delete de un user (Solo Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User desactivado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    /**
     * Soft-deletes (deactivates) the user identified by the given ID and
     * returns HTTP 204 (No Content) on success. Only users with the
     * ADMINISTRADOR authority may call this endpoint.
     *
     * @param id the unique identifier of the user to deactivate
     * @return a {@link org.springframework.http.ResponseEntity} with HTTP 204 (No Content) if the operation succeeded
     */
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        authService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
