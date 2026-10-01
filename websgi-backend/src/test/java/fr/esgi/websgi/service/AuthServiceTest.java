package fr.esgi.websgi.service;

import fr.esgi.websgi.dto.AuthResponse;
import fr.esgi.websgi.dto.LoginRequest;
import fr.esgi.websgi.dto.RegisterRequest;
import fr.esgi.websgi.dto.UserResponse;
import fr.esgi.websgi.entity.PasswordResetToken;
import fr.esgi.websgi.entity.Role;
import fr.esgi.websgi.entity.User;
import fr.esgi.websgi.exception.ApiExceptions;
import fr.esgi.websgi.repository.PasswordResetTokenRepository;
import fr.esgi.websgi.repository.UserRepository;
import fr.esgi.websgi.security.JwtService;
import fr.esgi.websgi.security.PasswordHasher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String EMAIL = "alice@example.com";
    private static final String RAW_PASSWORD = "motdepasse123";
    private static final String PASSWORD_HASH = "hash-bcrypt";
    private static final String JWT = "jwt-de-test";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository resetTokenRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldCreateUserWithUserRole_whenRegistering() {
        RegisterRequest request = registerRequest();
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(false);
        when(passwordHasher.hash(RAW_PASSWORD)).thenReturn(PASSWORD_HASH);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(jwtService.generateToken(1L, "USER")).thenReturn(JWT);

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPasswordHash()).isEqualTo(PASSWORD_HASH);
        assertThat(captor.getValue().getRole()).isEqualTo(Role.USER);
        assertThat(response.getToken()).isEqualTo(JWT);
        assertThat(response.getUser().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void shouldThrow_whenRegisteringWithAnEmailAlreadyUsed() {
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest()))
                .isInstanceOf(ApiExceptions.EmailAlreadyUsedException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldReturnToken_whenLoginCredentialsAreValid() {
        User user = user(1L, Role.USER);
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(user));
        when(passwordHasher.matches(RAW_PASSWORD, PASSWORD_HASH)).thenReturn(true);
        when(jwtService.generateToken(1L, "USER")).thenReturn(JWT);

        AuthResponse response = authService.login(loginRequest());

        assertThat(response.getToken()).isEqualTo(JWT);
        assertThat(response.getUser().getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrow_whenLoginEmailIsUnknown() {
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest()))
                .isInstanceOf(ApiExceptions.InvalidCredentialsException.class);
    }

    @Test
    void shouldThrow_whenLoginPasswordIsWrong() {
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(user(1L, Role.USER)));
        when(passwordHasher.matches(RAW_PASSWORD, PASSWORD_HASH)).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest()))
                .isInstanceOf(ApiExceptions.InvalidCredentialsException.class);
        verify(jwtService, never()).generateToken(any(), any());
    }

    @Test
    void shouldReturnUser_whenMeIsCalledWithKnownId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, Role.ADMIN)));

        UserResponse response = authService.me(1L);

        assertThat(response.getEmail()).isEqualTo(EMAIL);
        assertThat(response.getRole()).isEqualTo("admin");
    }

    @Test
    void shouldThrow_whenMeIsCalledWithUnknownId() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.me(99L))
                .isInstanceOf(ApiExceptions.UnauthenticatedException.class);
    }

    @Test
    void shouldSaveTokenValidForOneHour_whenForgotPasswordEmailIsKnown() {
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(user(1L, Role.USER)));

        authService.forgotPassword(EMAIL);

        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(resetTokenRepository).save(captor.capture());
        PasswordResetToken saved = captor.getValue();
        assertThat(saved.getToken()).isNotBlank();
        assertThat(saved.isUsed()).isFalse();
        assertThat(saved.getExpiresAt())
                .isAfter(LocalDateTime.now().plusMinutes(59))
                .isBefore(LocalDateTime.now().plusMinutes(61));
    }

    @Test
    void shouldDoNothing_whenForgotPasswordEmailIsUnknown() {
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.empty());

        authService.forgotPassword(EMAIL);

        verify(resetTokenRepository, never()).save(any());
    }

    @Test
    void shouldUpdatePasswordAndConsumeToken_whenResetTokenIsValid() {
        User user = user(1L, Role.USER);
        PasswordResetToken token = resetToken(user, LocalDateTime.now().plusMinutes(30), false);
        when(resetTokenRepository.findByToken("token-valide")).thenReturn(Optional.of(token));
        when(passwordHasher.hash("nouveau-mdp-123")).thenReturn("nouveau-hash");

        authService.resetPassword("token-valide", "nouveau-mdp-123");

        assertThat(user.getPasswordHash()).isEqualTo("nouveau-hash");
        assertThat(token.isUsed()).isTrue();
        verify(userRepository).save(user);
        verify(resetTokenRepository).save(token);
    }

    @Test
    void shouldThrow_whenResetTokenIsUnknown() {
        when(resetTokenRepository.findByToken("inconnu")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.resetPassword("inconnu", "nouveau-mdp-123"))
                .isInstanceOf(ApiExceptions.InvalidResetTokenException.class);
    }

    @Test
    void shouldThrow_whenResetTokenIsExpired() {
        PasswordResetToken token = resetToken(user(1L, Role.USER), LocalDateTime.now().minusMinutes(1), false);
        when(resetTokenRepository.findByToken("expire")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.resetPassword("expire", "nouveau-mdp-123"))
                .isInstanceOf(ApiExceptions.InvalidResetTokenException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrow_whenResetTokenIsAlreadyUsed() {
        PasswordResetToken token = resetToken(user(1L, Role.USER), LocalDateTime.now().plusMinutes(30), true);
        when(resetTokenRepository.findByToken("deja-utilise")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.resetPassword("deja-utilise", "nouveau-mdp-123"))
                .isInstanceOf(ApiExceptions.InvalidResetTokenException.class);
        verify(userRepository, never()).save(any());
    }

    private RegisterRequest registerRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Alice Martin");
        request.setEmail(EMAIL);
        request.setPassword(RAW_PASSWORD);
        return request;
    }

    private LoginRequest loginRequest() {
        LoginRequest request = new LoginRequest();
        request.setEmail(EMAIL);
        request.setPassword(RAW_PASSWORD);
        return request;
    }

    private User user(Long id, Role role) {
        User user = new User();
        user.setId(id);
        user.setFullName("Alice Martin");
        user.setEmail(EMAIL);
        user.setPasswordHash(PASSWORD_HASH);
        user.setRole(role);
        return user;
    }

    private PasswordResetToken resetToken(User user, LocalDateTime expiresAt, boolean used) {
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setExpiresAt(expiresAt);
        token.setUsed(used);
        return token;
    }
}
