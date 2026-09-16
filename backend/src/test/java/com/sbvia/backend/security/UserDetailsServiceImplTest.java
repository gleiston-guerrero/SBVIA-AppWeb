package com.sbvia.backend.security;

import com.sbvia.backend.entity.Role;
import com.sbvia.backend.entity.User;
import com.sbvia.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository usuarioRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void cargaUsuarioActivoPorCorreo() {
        User usuario = usuario(false);
        when(usuarioRepository.findByCorreoIgnoreCaseOrNombreUsuarioIgnoreCase(usuario.getCorreo(), usuario.getCorreo()))
                .thenReturn(Optional.of(usuario));

        var resultado = userDetailsService.loadUserByUsername(usuario.getCorreo());

        assertThat(resultado.getUsername()).isEqualTo(usuario.getCorreo());
        assertThat(resultado.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_USER");
    }

    @Test
    void cargaUsuarioActivoPorNombreUsuario() {
        User usuario = usuario(false);
        when(usuarioRepository.findByCorreoIgnoreCaseOrNombreUsuarioIgnoreCase(usuario.getNombreUsuario(), usuario.getNombreUsuario()))
                .thenReturn(Optional.of(usuario));

        var resultado = userDetailsService.loadUserByUsername(usuario.getNombreUsuario());

        assertThat(resultado.getUsername()).isEqualTo(usuario.getCorreo());
        assertThat(resultado.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_USER");
    }

    @Test
    void rechazaUsuarioInactivo() {
        User usuario = usuario(true);
        when(usuarioRepository.findByCorreoIgnoreCaseOrNombreUsuarioIgnoreCase(usuario.getCorreo(), usuario.getCorreo()))
                .thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(usuario.getCorreo()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("bloqueada");
    }

    @Test
    void rechazaIdentificadorNoRegistrado() {
        when(usuarioRepository.findByCorreoIgnoreCaseOrNombreUsuarioIgnoreCase("ausente@sbvia.test", "ausente@sbvia.test"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("ausente@sbvia.test"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("ausente@sbvia.test");
    }

    private User usuario(boolean accountLocked) {
        return User.builder()
                .nombres("Conductor")
                .apellidos("Demo")
                .nombreUsuario("cdemop")
                .correo("conductor@sbvia.test")
                .contrasenaHash("hash-seguro")
                .accountLocked(accountLocked)
                .rol(Role.builder().name("ROLE_USER").build())
                .build();
    }
}
