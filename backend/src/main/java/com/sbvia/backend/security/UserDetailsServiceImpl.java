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

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository usuarioRepository;

    @Override
    /**
     * Método público.
     */
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
