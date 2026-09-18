package com.sbvia.backend.service;

import com.sbvia.backend.dto.*;
import com.sbvia.backend.entity.UserState;
import com.sbvia.backend.entity.Role;
import com.sbvia.backend.entity.User;
import com.sbvia.backend.exception.DuplicateEmailException;
import com.sbvia.backend.repository.UserStateRepository;
import com.sbvia.backend.repository.UserRepository;
import com.sbvia.backend.repository.RoleRepository;
import com.sbvia.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>AuthService class.</p>
 *
 * @author Keitho_
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    /** Default constructor for AuthService. */
    public AuthService() {}

    private final UserRepository usuarioRepository;
    private final RoleRepository rolRepository;
    private final UserStateRepository estadoUsuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;
    private final UsernameGeneratorService usernameGeneratorService;

    @Transactional
    /**
     * Método público.
     *
     * @param request a {@link com.sbvia.backend.dto.RegisterRequest} object
     * @return a {@link com.sbvia.backend.dto.AuthResponse} object
     */
    /** Javadoc for this element. */
    public AuthResponse registro(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(
                    "Ya existe un user registrado con el email: " + request.getEmail());
        }

        Role rolPorDefecto = rolRepository.findByName("PARTICIPANTE")
                .orElseGet(() -> rolRepository.findAll().stream()
                        .filter(r -> r.getName().contains("PARTICIPANTE") || r.getName().contains("USER"))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No se encontró el role PARTICIPANTE")));

        // id_estado_usuario es NOT NULL: toda cuenta nueva nace en estado ACTIVO.
        UserState estadoActivo = estadoUsuarioRepository.findByName("ACTIVO")
                .orElseThrow(() -> new IllegalStateException("No se encontró el estado ACTIVO"));

        // Generación de nombre_usuario automático estilo SGA UTEQ
        String base = usernameGeneratorService.generateBase(request.getFirstName(), request.getLastName());
        List<String> existentes = new ArrayList<>(usuarioRepository.findNombresUsuarioSimilares(base));
        String nombreUsuarioGenerado = usernameGeneratorService.generateNextAvailable(base, existentes);

        User user = User.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .username(nombreUsuarioGenerado)
                .email(request.getEmail().trim())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(rolPorDefecto)
                .userState(estadoActivo)
                .build();

        user = usuarioRepository.save(user);

        UserDetails userDetails = buildUserDetails(user);
        String rolNombre = user.getRole().getName();
        String accessToken = jwtService.generateAccessToken(userDetails, user.getUserId().longValue(), rolNombre);
        String refreshToken = jwtService.generateRefreshToken(userDetails, user.getUserId().longValue());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessExpirationMs() / 1000)
                .tokenType("Bearer")
                .user(mapToDTO(user))
                .build();
    }

    /**
     * Método público.
     *
     * @param request a {@link com.sbvia.backend.dto.LoginRequest} object
     * @return a {@link com.sbvia.backend.dto.AuthResponse} object
     */
    public AuthResponse login(LoginRequest request) {
        String identificador = request.getIdentificador();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        identificador,
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = usuarioRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(identificador, identificador)
                .orElseThrow(() -> new IllegalArgumentException("User no encontrado con identificador: " + identificador));

        String rolNombre = user.getRole().getName();
        String accessToken = jwtService.generateAccessToken(userDetails, user.getUserId().longValue(), rolNombre);
        String refreshToken = jwtService.generateRefreshToken(userDetails, user.getUserId().longValue());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessExpirationMs() / 1000)
                .tokenType("Bearer")
                .user(mapToDTO(user))
                .build();
    }

    /**
     * Método público.
     *
     * @param token a {@link java.lang.String} object
     */
    public void logout(String token) {
        String jti = jwtService.extractJti(token);
        long remainingMs = jwtService.getExpirationRemainingMs(token);
        if (remainingMs > 0) {
            tokenBlacklistService.blacklistToken(jti, remainingMs);
        }
    }

    /**
     * Método público.
     *
     * @param refreshToken a {@link java.lang.String} object
     * @return a {@link com.sbvia.backend.dto.AuthResponse} object
     */
    public AuthResponse refresh(String refreshToken) {
        String tokenType = jwtService.extractTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new IllegalArgumentException("El token proporcionado no es un refresh token");
        }

        String jti = jwtService.extractJti(refreshToken);
        if (tokenBlacklistService.isTokenBlacklisted(jti)) {
            throw new IllegalArgumentException("El refresh token ha sido revocado");
        }

        String subject = jwtService.extractSubject(refreshToken);
        User user = usuarioRepository.findById(Integer.parseInt(subject))
                .orElseThrow(() -> new IllegalArgumentException("User no encontrado"));

        UserDetails userDetails = buildUserDetails(user);
        String rolNombre = user.getRole().getName();
        String newAccessToken = jwtService.generateAccessToken(userDetails, user.getUserId().longValue(), rolNombre);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessExpirationMs() / 1000)
                .tokenType("Bearer")
                .user(mapToDTO(user))
                .build();
    }

    /**
     * Método público.
     *
     * @param identificador a {@link java.lang.String} object
     * @return a {@link com.sbvia.backend.dto.UserDTO} object
     */
    public UserDTO getCurrentUser(String identificador) {
        User user = usuarioRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(identificador, identificador)
                .orElseThrow(() -> new IllegalArgumentException("User no encontrado"));
        return mapToDTO(user);
    }

    /**
     * <p>listUsers.</p>
     *
     * @param pageable a {@link org.springframework.data.domain.Pageable} object
     * @return a {@link org.springframework.data.domain.Page} object
     */
    public org.springframework.data.domain.Page<UserDTO> listUsers(org.springframework.data.domain.Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(this::mapToDTO);
    }

    @Transactional
    /**
     * Método público.
     *
     * @param id a {@link java.lang.Integer} object
     * @param nombreRol a {@link java.lang.String} object
     * @return a {@link com.sbvia.backend.dto.UserDTO} object
     */
    /** Javadoc for this element. */
    public UserDTO cambiarRol(Integer id, String nombreRol) {
        User user = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User no encontrado con ID: " + id));

        Role nuevoRol = rolRepository.findByName(nombreRol)
                .orElseThrow(() -> new IllegalArgumentException("Role no encontrado: " + nombreRol));

        user.setRole(nuevoRol);
        user = usuarioRepository.save(user);
        return mapToDTO(user);
    }

    @Transactional
    /**
     * Método público.
     *
     * @param id a {@link java.lang.Integer} object
     * @param request a {@link com.sbvia.backend.dto.UpdateUserRequest} object
     * @return a {@link com.sbvia.backend.dto.UserDTO} object
     */
    /** Javadoc for this element. */
    public UserDTO updateUser(Integer id, UpdateUserRequest request) {
        User user = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User no encontrado con ID: " + id));

        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateEmailException("Ya existe un user registrado con el email: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());

        user = usuarioRepository.save(user);
        return mapToDTO(user);
    }

    @Transactional
    /**
     * Método público.
     *
     * @param identificador a {@link java.lang.String} object
     * @param request a {@link com.sbvia.backend.dto.UpdateProfileRequest} object
     * @return a {@link com.sbvia.backend.dto.UserDTO} object
     */
    /** Javadoc for this element. */
    public UserDTO updateCurrentUserProfile(String identificador, UpdateProfileRequest request) {
        User user = usuarioRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(identificador, identificador)
                .orElseThrow(() -> new IllegalArgumentException("User no encontrado"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());

        user = usuarioRepository.save(user);
        return mapToDTO(user);
    }

    @Transactional
    /**
     * Método público.
     *
     * @param id a {@link java.lang.Integer} object
     */
    /** Javadoc for this element. */
    public void deleteUser(Integer id) {
        User user = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User no encontrado con ID: " + id));
        user.setAccountLocked(true);
        usuarioRepository.save(user);
    }

    /**
     * Método público.
     *
     * @return a long
     */
    public long getRefreshExpirationSeconds() {
        return jwtService.getRefreshExpirationMs() / 1000;
    }

    private UserDetails buildUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                        user.getRole().getName()))
        );
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .phone(user.getPhone())
                .accountLocked(user.isAccountLocked())
                .build();
    }
}
