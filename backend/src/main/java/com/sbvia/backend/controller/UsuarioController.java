package com.sbvia.backend.controller;

import com.sbvia.backend.dto.UsuarioDTO;
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
import com.sbvia.backend.dto.ActualizarUsuarioRequest;
import com.sbvia.backend.dto.CambiarRolRequest;
import jakarta.validation.Valid;

/**
 * Controlador REST para operaciones de usuario autenticado.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final AuthService authService;

    /**
     * GET /api/usuarios/me — Devuelve el perfil del usuario autenticado.
     *
     * @param authentication el objeto de autenticación actual que contiene las credenciales del usuario
     * @return una respuesta HTTP que contiene el objeto UsuarioDTO con la información del perfil del usuario
     */
    @GetMapping("/me")
    @Operation(summary = "Perfil del usuario", description = "Devuelve los datos del usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Datos devueltos exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado o token expirado")
    })
    public ResponseEntity<UsuarioDTO> getPerfilActual(Authentication authentication) {
        String email = authentication.getName();
        UsuarioDTO usuario = authService.getUsuarioActual(email);
        return ResponseEntity.ok(usuario);
    }

    /**
     * PUT /api/usuarios/me — Actualiza el perfil del usuario autenticado.
     *
     * @param authentication el objeto de autenticación actual
     * @param request los datos a actualizar en el perfil del usuario
     * @return una respuesta HTTP con el objeto UsuarioDTO actualizado
     */
    @PutMapping("/me")
    @Operation(summary = "Actualizar perfil", description = "Actualiza los datos del usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado o token expirado")
    })
    public ResponseEntity<UsuarioDTO> actualizarPerfilActual(
            Authentication authentication,
            @Valid @RequestBody com.sbvia.backend.dto.ActualizarPerfilRequest request) {
        String email = authentication.getName();
        UsuarioDTO actualizado = authService.actualizarPerfilActual(email, request);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * GET /api/usuarios — Lista todos los usuarios con paginación (Solo Admin).
     *
     * @param pageable objeto que contiene la información de paginación solicitada (página, tamaño)
     * @return una página (Page) de objetos UsuarioDTO que representan a los usuarios en el sistema
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Operation(summary = "Listar usuarios", description = "Lista todos los usuarios (Solo Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista devuelta exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Page<UsuarioDTO>> listarUsuarios(Pageable pageable) {
        return ResponseEntity.ok(authService.listarUsuarios(pageable));
    }

    /**
     * PUT /api/usuarios/{id}/rol — Cambia el rol de un usuario (Solo Admin).
     *
     * @param id el identificador único del usuario al que se le cambiará el rol
     * @param request el objeto que contiene el nombre del nuevo rol a asignar
     * @return una respuesta HTTP con el objeto UsuarioDTO reflejando el rol actualizado
     */
    @PutMapping("/{id}/rol")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Operation(summary = "Cambiar rol de usuario", description = "Asigna un nuevo rol a un usuario (Solo Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<UsuarioDTO> cambiarRol(
            @PathVariable Integer id,
            @Valid @RequestBody CambiarRolRequest request) {
        UsuarioDTO actualizado = authService.cambiarRol(id, request.getNombreRol());
        return ResponseEntity.ok(actualizado);
    }

    /**
     * PUT /api/usuarios/{id} — Actualiza los datos de un usuario (Solo Admin).
     *
     * @param id el identificador único del usuario a modificar
     * @param request el objeto con los nuevos datos a actualizar (nombres, apellidos, estado)
     * @return una respuesta HTTP con el objeto UsuarioDTO actualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Operation(summary = "Actualizar datos de usuario", description = "Modifica los datos de un usuario (Solo Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    public ResponseEntity<UsuarioDTO> actualizarUsuario(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarUsuarioRequest request) {
        UsuarioDTO actualizado = authService.actualizarUsuario(id, request);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * DELETE /api/usuarios/{id} — Desactiva un usuario (Solo Admin).
     *
     * @param id el identificador único del usuario a desactivar
     * @return una respuesta HTTP 204 sin contenido si la operación fue exitosa
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Operation(summary = "Eliminar (desactivar) usuario", description = "Soft delete de un usuario (Solo Admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario desactivado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
        authService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
