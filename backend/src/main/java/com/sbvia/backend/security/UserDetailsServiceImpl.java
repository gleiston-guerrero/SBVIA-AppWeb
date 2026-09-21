package com.sbvia.backend.security;

import com.sbvia.backend.entity.User;
import com.sbvia.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Loads user accounts from the database for Spring Security.
 *
 * @author Keitho_
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository usuarioRepository;

    /**
     * {@inheritDoc}
     *
     * Loads a user by the given email or username. It rejects blank
     * identifiers, looks the user up case-insensitively by email or username,
     * refuses locked accounts, and builds the Spring Security user carrying the
     * account's role as its single granted authority.
     *
     * @param identificador the email or username that identifies the user
     * @return a {@link org.springframework.security.core.userdetails.UserDetails} with the user's credentials and role
     * @throws UsernameNotFoundException if the identifier is blank, no matching user exists, or the account is locked
     */
    @Override
    public UserDetails loadUserByUsername(String identificador) throws UsernameNotFoundException {
        if (identificador == null || identificador.isBlank()) {
            throw new UsernameNotFoundException("Identificador de user no proporcionado");
        }

        User user = usuarioRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(identificador.trim(), identificador.trim())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User no encontrado con identificador: " + identificador));

        if (user.isAccountLocked()) {
            throw new UsernameNotFoundException("La cuenta del user está bloqueada");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority(user.getRole().getName()))
        );
    }
}
