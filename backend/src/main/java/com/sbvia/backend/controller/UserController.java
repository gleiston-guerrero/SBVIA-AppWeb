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
 * Controlador REST para operaciones de user autenticado.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operaciones del user autenticado")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final AuthService authService;

    /**
     * GET /api/users/me — Devuelve el perfil del user autenticado.
     *
     * @param authentication el objeto de autenticación actual que contiene las credenciales del user
     * @return una respuesta HTTP que contiene el objeto UserDTO con la información del perfil del user
     */
    @GetMapping("/me")
    @Operation(summary = "Perfil del user", description = "Devuelve los datos del user autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Datos devueltos exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado o token expirado")
    })
    public ResponseEntity<UserDTO> getPerfilActual(Authentication authentication) {
        String email = authentication.getName();
        UserDTO user = authService.getCurrentUser(email);
        return ResponseEntity.ok(user);
    }

    /**
     * PUT /api/users/me — Actualiza el perfil del user autenticado.
     *
     * @param authentication el objeto de autenticación actual
     * @param request los datos a actualizar en el perfil del user
     * @return una respuesta HTTP con el objeto UserDTO actualizado
     */
    @PutMapping("/me")
    @Operation(summary = "Actualizar perfil", description = "Actualiza los datos del user autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado o token expirado")
    })
    public ResponseEntity<UserDTO> updateCurrentUserProfile(
            Authentication authentication,
            @Valid @RequestBody com.sbvia.backend.dto.UpdateProfileRequest request) {
        String email = authentication.getName();
        UserDTO actualizado = authService.updateCurrentUserProfile(email, request);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * GET /api/users — Lista todos los users con paginación (Solo Admin).
     *
     * @param pageable objeto que contiene la información de paginación solicitada (página, tamaño)
     * @return una página (Page) de objetos UserDTO que representan a los users en el sistema
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Operation(summary = "Listar users", description = "Lista todos los users (Solo Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista devuelta exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Page<UserDTO>> listUsers(Pageable pageable) {
        return ResponseEntity.ok(authService.listUsers(pageable));
    }

    /**
     * PUT /api/users/{id}/role — Cambia el role de un user (Solo Admin).
     *
     * @param id el identificador único del user al que se le cambiará el role
     * @param request el objeto que contiene el name del nuevo role a asignar
     * @return una respuesta HTTP con el objeto UserDTO reflejando el role actualizado
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Operation(summary = "Cambiar role de user", description = "Asigna un nuevo role a un user (Solo Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role actualizado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<UserDTO> cambiarRol(
            @PathVariable Integer id,
            @Valid @RequestBody ChangeRoleRequest request) {
        UserDTO actualizado = authService.cambiarRol(id, request.getNombreRol());
        return ResponseEntity.ok(actualizado);
    }

    /**
     * PUT /api/users/{id} — Actualiza los datos de un user (Solo Admin).
     *
     * @param id el identificador único del user a modificar
     * @param request el objeto con los nuevos datos a actualizar (firstName, lastName, estado)
     * @return una respuesta HTTP con el objeto UserDTO actualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Operation(summary = "Actualizar datos de user", description = "Modifica los datos de un user (Solo Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User actualizado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserDTO actualizado = authService.updateUser(id, request);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * DELETE /api/users/{id} — Desactiva un user (Solo Admin).
     *
     * @param id el identificador único del user a desactivar
     * @return una respuesta HTTP 204 sin contenido si la operación fue exitosa
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Operation(summary = "Eliminar (desactivar) user", description = "Soft delete de un user (Solo Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User desactivado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        authService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
