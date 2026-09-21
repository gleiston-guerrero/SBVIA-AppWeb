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
    private User user;

    @BeforeEach
    void prepararUsuario() {
        rol = Role.builder().roleId(1).name("ROLE_USER").build();
        user = User.builder()
                .userId(9)
                .firstName("Ana")
                .lastName("Pérez")
                .username("aperez")
                .email("ana@sbvia.test")
                .passwordHash("hash")
                .role(rol)
                .accountLocked(false)
                .build();
    }

    @Test
    void rechazaUnRegistroConCorreoDuplicado() {
        RegisterRequest request = register();
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining(request.getEmail());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void rechazaUnRegistroSiFaltaElRolPredeterminado() {
        RegisterRequest request = register();
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(rolRepository.findByName("PARTICIPANTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PARTICIPANTE");
    }

    @Test
    void registraUsuarioConNombreUsuarioGeneradoExitosamente() {
        RegisterRequest request = register();
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(rolRepository.findByName("PARTICIPANTE")).thenReturn(Optional.of(rol));
        when(estadoUsuarioRepository.findByName("ACTIVO")).thenReturn(Optional.of(new UserState()));
        when(usernameGeneratorService.generateBase(request.getFirstName(), request.getLastName())).thenReturn("aperez");
        when(usuarioRepository.findSimilarUsernames("aperez")).thenReturn(List.of());
        when(usernameGeneratorService.generateNextAvailable("aperez", List.of())).thenReturn("aperez");
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed-pwd");
        when(usuarioRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(), any())).thenReturn("refresh-token");
        when(jwtService.getAccessExpirationMs()).thenReturn(3600000L);

        var response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getUser().getUsername()).isEqualTo("aperez");
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
        when(usuarioRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(user.getEmail(), user.getEmail()))
                .thenReturn(Optional.of(user));

        assertThat(authService.getCurrentUser(user.getEmail()).getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    void cambiaElRolDelUsuario() {
        Role administrador = Role.builder().roleId(2).name("ROLE_ADMIN").build();
        when(usuarioRepository.findById(9)).thenReturn(Optional.of(user));
        when(rolRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(administrador));
        when(usuarioRepository.save(user)).thenReturn(user);

        assertThat(authService.changeRole(9, "ROLE_ADMIN").getRole()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void haceEliminacionLogicaDelUsuario() {
        when(usuarioRepository.findById(9)).thenReturn(Optional.of(user));

        authService.deleteUser(9);

        assertThat(user.isAccountLocked()).isTrue();
        verify(usuarioRepository).save(user);
    }

    private RegisterRequest register() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Ana");
        request.setLastName("Pérez");
        request.setEmail("ana@sbvia.test");
        request.setPassword("Password123!");
        return request;
    }
}
