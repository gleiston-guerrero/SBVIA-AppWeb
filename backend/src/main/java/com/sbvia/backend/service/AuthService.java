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

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Authenticates users, issues tokens and manages the session.
 *
 * @author Keitho_
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository usuarioRepository;
    private final RoleRepository rolRepository;
    private final UserStateRepository estadoUsuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;
    private final UsernameGeneratorService usernameGeneratorService;
    private final EntityManager entityManager;

    /**
     * Registers a new user with the PARTICIPANT role and the ACTIVO state, generating
     * a unique username, and issues JWT access and refresh tokens for the new account.
     *
     * @param request the registration data with the user's first name, last name, email, phone, and password
     * @return the authentication response with the generated tokens and the created user
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(
                    "Ya existe un user registrado con el email: " + request.getEmail());
        }

        Role rolPorDefecto = rolRepository.findByName("PARTICIPANTE")
                .orElseGet(() -> rolRepository.findAll().stream()
                        .filter(r -> r.getName().contains("PARTICIPANTE") || r.getName().contains("USER"))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No se encontró el role PARTICIPANTE")));

        // id_estado_usuario is NOT NULL: every new account starts in the ACTIVO state.
        UserState estadoActivo = estadoUsuarioRepository.findByName("ACTIVO")
                .orElseThrow(() -> new IllegalStateException("No se encontró el estado ACTIVO"));

        // Automatic nombre_usuario generation, UTEQ SGA style
        String base = usernameGeneratorService.generateBase(request.getFirstName(), request.getLastName());
        List<String> existentes = new ArrayList<>(usuarioRepository.findSimilarUsernames(base));
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
     * Authenticates a user by email or username and password, and issues JWT access and
     * refresh tokens for the authenticated account.
     *
     * @param request the login credentials containing the user identifier and the password
     * @return the authentication response with the generated tokens and the logged-in user
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
     * Revokes the given JWT token by blacklisting its unique identifier for the time
     * remaining until its expiration.
     *
     * @param token the JWT access token to revoke
     */
    public void logout(String token) {
        String jti = jwtService.extractJti(token);
        long remainingMs = jwtService.getExpirationRemainingMs(token);
        if (remainingMs > 0) {
            tokenBlacklistService.blacklistToken(jti, remainingMs);
        }
    }

    /**
     * Validates the given refresh token, rejects revoked tokens, and issues a new access
     * token for the same user.
     *
     * @param refreshToken the JWT refresh token used to obtain a new access token
     * @return the authentication response with the new access token and the user
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
     * Loads the user identified by email or username and returns its data as a DTO.
     *
     * @param identificador the email or username of the user to look up
     * @return the DTO with the data of the found user
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

    /**
     * Changes the role of the user with the given id to the role with the given name.
     *
     * @param id the id of the user whose role is changed
     * @param nombreRol the name of the new role to assign
     * @return the DTO with the updated user
     */
    @Transactional
    public UserDTO changeRole(Integer id, String nombreRol) {
        User user = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User no encontrado con ID: " + id));

        Role nuevoRol = rolRepository.findByName(nombreRol)
                .orElseThrow(() -> new IllegalArgumentException("Role no encontrado: " + nombreRol));

        user.setRole(nuevoRol);
        user = usuarioRepository.save(user);
        return mapToDTO(user);
    }

    /**
     * Updates the basic data of the user with the given id, checking email uniqueness
     * when the email changes.
     *
     * @param id the id of the user to update
     * @param request the new first name, last name, phone, and email values
     * @return the DTO with the updated user
     */
    @Transactional
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

    /**
     * Updates the profile data of the user identified by email or username.
     *
     * @param identificador the email or username of the user whose profile is updated
     * @param request the new first name, last name, and phone values
     * @return the DTO with the updated user
     */
    @Transactional
    public UserDTO updateCurrentUserProfile(String identificador, UpdateProfileRequest request) {
        User user = usuarioRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(identificador, identificador)
                .orElseThrow(() -> new IllegalArgumentException("User no encontrado"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());

        user = usuarioRepository.save(user);
        return mapToDTO(user);
    }

    /**
     * Soft-deletes the user with the given id by locking its account.
     *
     * @param id the id of the user to deactivate
     */
    @Transactional
    public void deleteUser(Integer id) {
        User user = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User no encontrado con ID: " + id));
        user.setAccountLocked(true);
        usuarioRepository.save(user);
    }

    /**
     * Returns the configured refresh token lifetime expressed in seconds.
     *
     * @return the refresh token expiration time in seconds
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

    /**
     * Calls sp_actualizar_usuarios_inactivos (RF-06): deactivates in bulk the accounts
     * whose ultimo_acceso is earlier than the given cutoff date, leaving a trace in bitacora_auditoria.
     *
     * @param fechaLimite cutoff date; users whose ultimo_acceso is earlier are deactivated
     * @return the number of deactivated users
     */
    @Transactional
    public int deactivateInactiveUsers(LocalDate fechaLimite) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_actualizar_usuarios_inactivos")
                .registerStoredProcedureParameter("p_fecha_limite", java.sql.Date.class, ParameterMode.IN)
                .registerStoredProcedureParameter("actualizados", Integer.class, ParameterMode.OUT)
                .setParameter("p_fecha_limite", java.sql.Date.valueOf(fechaLimite));
        query.execute();
        Integer actualizados = (Integer) query.getOutputParameterValue("actualizados");
        return actualizados != null ? actualizados : 0;
    }
}
