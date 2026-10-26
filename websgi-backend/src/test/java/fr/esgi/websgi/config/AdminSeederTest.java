package fr.esgi.websgi.config;

import fr.esgi.websgi.entity.Role;
import fr.esgi.websgi.entity.User;
import fr.esgi.websgi.repository.UserRepository;
import fr.esgi.websgi.security.PasswordHasher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminSeederTest {

    private static final String EMAIL = "admin@websgi.fr";
    private static final String PASSWORD = "mot-de-passe-admin";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    private AdminSeeder seeder(String password) {
        return new AdminSeeder(userRepository, passwordHasher, EMAIL, password);
    }

    @Test
    void shouldDoNothing_whenSeedPasswordIsBlank() {
        seeder("").run(null);

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrow_whenSeedPasswordIsTooShort() {
        assertThatThrownBy(() -> seeder("court").run(null))
                .isInstanceOf(IllegalStateException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldCreateAdmin_whenAccountDoesNotExist() {
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.empty());
        when(passwordHasher.hash(PASSWORD)).thenReturn("hash-admin");

        seeder(PASSWORD).run(null);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo(EMAIL);
        assertThat(captor.getValue().getRole()).isEqualTo(Role.ADMIN);
        assertThat(captor.getValue().getPasswordHash()).isEqualTo("hash-admin");
    }

    @Test
    void shouldReplacePassword_whenExistingAdminHasAnotherPassword() {
        User existing = existingAdmin("ancien-hash");
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(existing));
        when(passwordHasher.matches(PASSWORD, "ancien-hash")).thenReturn(false);
        when(passwordHasher.hash(PASSWORD)).thenReturn("nouveau-hash");

        seeder(PASSWORD).run(null);

        assertThat(existing.getPasswordHash()).isEqualTo("nouveau-hash");
        verify(userRepository).save(existing);
    }

    @Test
    void shouldKeepHash_whenExistingAdminAlreadyHasThePassword() {
        User existing = existingAdmin("hash-actuel");
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(existing));
        when(passwordHasher.matches(PASSWORD, "hash-actuel")).thenReturn(true);

        seeder(PASSWORD).run(null);

        assertThat(existing.getPasswordHash()).isEqualTo("hash-actuel");
        verify(passwordHasher, never()).hash(any());
    }

    private User existingAdmin(String hash) {
        User user = new User();
        user.setId(1L);
        user.setEmail(EMAIL);
        user.setRole(Role.ADMIN);
        user.setPasswordHash(hash);
        return user;
    }
}
