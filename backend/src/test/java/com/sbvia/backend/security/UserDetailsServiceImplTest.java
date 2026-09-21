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
    void loadsActiveUserByEmail() {
        User user = user(false);
        when(usuarioRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(user.getEmail(), user.getEmail()))
                .thenReturn(Optional.of(user));

        var resultado = userDetailsService.loadUserByUsername(user.getEmail());

        assertThat(resultado.getUsername()).isEqualTo(user.getEmail());
        assertThat(resultado.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_USER");
    }

    @Test
    void loadsActiveUserByUsername() {
        User user = user(false);
        when(usuarioRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(user.getUsername(), user.getUsername()))
                .thenReturn(Optional.of(user));

        var resultado = userDetailsService.loadUserByUsername(user.getUsername());

        assertThat(resultado.getUsername()).isEqualTo(user.getEmail());
        assertThat(resultado.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_USER");
    }

    @Test
    void rejectsInactiveUser() {
        User user = user(true);
        when(usuarioRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase(user.getEmail(), user.getEmail()))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(user.getEmail()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("bloqueada");
    }

    @Test
    void rejectsUnregisteredIdentifier() {
        when(usuarioRepository.findByEmailIgnoreCaseOrUsernameIgnoreCase("ausente@sbvia.test", "ausente@sbvia.test"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("ausente@sbvia.test"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("ausente@sbvia.test");
    }

    private User user(boolean accountLocked) {
        return User.builder()
                .firstName("Conductor")
                .lastName("Demo")
                .username("cdemop")
                .email("conductor@sbvia.test")
                .passwordHash("hash-seguro")
                .accountLocked(accountLocked)
                .role(Role.builder().name("ROLE_USER").build())
                .build();
    }
}
