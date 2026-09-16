package com.sbvia.backend.service;

import com.sbvia.backend.dto.RegisterRequest;
import com.sbvia.backend.entity.UserState;
import com.sbvia.backend.entity.Role;
import com.sbvia.backend.entity.User;
import com.sbvia.backend.exception.DuplicateEmailException;
import com.sbvia.backend.repository.RoleRepository;
import com.sbvia.backend.repository.UserRepository;
import com.sbvia.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository usuarioRepository;
    @Mock RoleRepository rolRepository;
    @Mock com.sbvia.backend.repository.UserStateRepository estadoUsuarioRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @Mock AuthenticationManager authenticationManager;
    @Mock TokenBlacklistService tokenBlacklistService;
    @Mock UsernameGeneratorService usernameGeneratorService;
    @InjectMocks AuthService authService;

    private Role rol;
    private User usuario;

    @BeforeEach
    void prepararUsuario() {
        rol = Role.builder().roleId(1).name("ROLE_USER").build();
        usuario = User.builder()
                .idUsuario(9)
                .nombres("Ana")
                .apellidos("Pérez")
                .nombreUsuario("aperez")
                .correo("ana@sbvia.test")
                .contrasenaHash("hash")
                .rol(rol)
                .accountLocked(false)
                .build();
    }

    @Test
    void rechazaUnRegistroConCorreoDuplicado() {
        RegisterRequest request = registro();
        when(usuarioRepository.existsByCorreo(request.getCorreo())).thenReturn(true);

        assertThatThrownBy(() -> authService.registro(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining(request.getCorreo());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void rechazaUnRegistroSiFaltaElRolPredeterminado() {
        RegisterRequest request = registro();
        when(usuarioRepository.existsByCorreo(request.getCorreo())).thenReturn(false);
        when(rolRepository.findByNombre("PARTICIPANTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.registro(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PARTICIPANTE");
    }

    @Test
    void registraUsuarioConNombreUsuarioGeneradoExitosamente() {
        RegisterRequest request = registro();
        when(usuarioRepository.existsByCorreo(request.getCorreo())).thenReturn(false);
        when(rolRepository.findByNombre("PARTICIPANTE")).thenReturn(Optional.of(rol));
        when(estadoUsuarioRepository.findByNombre("ACTIVO")).thenReturn(Optional.of(new UserState()));
        when(usernameGeneratorService.generarBase(request.getNombres(), request.getApellidos())).thenReturn("aperez");
        when(usuarioRepository.findNombresUsuarioSimilares("aperez")).thenReturn(List.of());
        when(usernameGeneratorService.generarSiguienteDisponible("aperez", List.of())).thenReturn("aperez");
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed-pwd");
        when(usuarioRepository.save(any(User.class))).thenReturn(usuario);
        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(), any())).thenReturn("refresh-token");
        when(jwtService.getAccessExpirationMs()).thenReturn(3600000L);

        var response = authService.registro(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getUsuario().getNombreUsuario()).isEqualTo("aperez");
    }

    @Test
    void revocaUnTokenQueTodaviaNoHaExpirado() {
        when(jwtService.extractJti("token")).thenReturn("jti-1");
        when(jwtService.getExpirationRemainingMs("token")).thenReturn(25_000L);

        authService.logout("token");

        verify(tokenBlacklistService).blacklistToken("jti-1", 25_000L);
    }

    @Test
    void noRevocaUnTokenQueYaExpiro() {
        when(jwtService.extractJti("token")).thenReturn("jti-1");
        when(jwtService.getExpirationRemainingMs("token")).thenReturn(0L);

        authService.logout("token");

        verify(tokenBlacklistService, never()).blacklistToken(any(), any(Long.class));
    }

    @Test
    void rechazaUsarUnAccessTokenParaRenovar() {
        when(jwtService.extractTokenType("token")).thenReturn("access");

        assertThatThrownBy(() -> authService.refresh("token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no es un refresh token");
    }

    @Test
    void rechazaUnRefreshTokenRevocado() {
        when(jwtService.extractTokenType("token")).thenReturn("refresh");
        when(jwtService.extractJti("token")).thenReturn("jti-1");
        when(tokenBlacklistService.isTokenBlacklisted("jti-1")).thenReturn(true);

        assertThatThrownBy(() -> authService.refresh("token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("revocado");
    }

    @Test
    void obtieneElUsuarioActualPorIdentificador() {
        when(usuarioRepository.findByCorreoIgnoreCaseOrNombreUsuarioIgnoreCase(usuario.getCorreo(), usuario.getCorreo()))
                .thenReturn(Optional.of(usuario));

        assertThat(authService.getUsuarioActual(usuario.getCorreo()).getRol()).isEqualTo("ROLE_USER");
    }

    @Test
    void cambiaElRolDelUsuario() {
        Role administrador = Role.builder().roleId(2).name("ROLE_ADMIN").build();
        when(usuarioRepository.findById(9)).thenReturn(Optional.of(usuario));
        when(rolRepository.findByNombre("ROLE_ADMIN")).thenReturn(Optional.of(administrador));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        assertThat(authService.cambiarRol(9, "ROLE_ADMIN").getRol()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void haceEliminacionLogicaDelUsuario() {
        when(usuarioRepository.findById(9)).thenReturn(Optional.of(usuario));

        authService.eliminarUsuario(9);

        assertThat(usuario.isAccountLocked()).isTrue();
        verify(usuarioRepository).save(usuario);
    }

    private RegisterRequest registro() {
        RegisterRequest request = new RegisterRequest();
        request.setNombres("Ana");
        request.setApellidos("Pérez");
        request.setCorreo("ana@sbvia.test");
        request.setPassword("Password123!");
        return request;
    }
}
