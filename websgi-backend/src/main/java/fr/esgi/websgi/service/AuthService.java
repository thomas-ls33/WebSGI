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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordHasher passwordHasher;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordResetTokenRepository resetTokenRepository,
            PasswordHasher passwordHasher,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.passwordHasher = passwordHasher;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ApiExceptions.EmailAlreadyUsedException(request.getEmail());
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordHasher.hash(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(ApiExceptions.InvalidCredentialsException::new);

        if (!passwordHasher.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiExceptions.InvalidCredentialsException();
        }

        return buildAuthResponse(user);
    }

    public UserResponse me(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(ApiExceptions.UnauthenticatedException::new);
        return UserResponse.from(user);
    }

    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setUser(user);
            resetToken.setToken(UUID.randomUUID().toString());
            resetToken.setExpiresAt(LocalDateTime.now().plusHours(1));
            resetTokenRepository.save(resetToken);
            log.info("Token de réinitialisation pour {} : {}", user.getEmail(), resetToken.getToken());
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(token)
                .filter(t -> !t.isUsed())
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElseThrow(ApiExceptions.InvalidResetTokenException::new);

        User user = resetToken.getUser();
        user.setPasswordHash(passwordHasher.hash(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user.getId(), user.getRole().name());
        return new AuthResponse(token, UserResponse.from(user));
    }
}
