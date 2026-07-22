package fr.esgi.websgi.controller;

import fr.esgi.websgi.dto.*;
import fr.esgi.websgi.exception.ApiExceptions;
import fr.esgi.websgi.security.AuthenticatedUser;
import fr.esgi.websgi.security.CurrentUserContext;
import fr.esgi.websgi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        AuthenticatedUser current = requireAuthenticated();
        return ResponseEntity.ok(authService.me(current.getId()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getPassword());
        return ResponseEntity.ok().build();
    }

    private AuthenticatedUser requireAuthenticated() {
        AuthenticatedUser current = CurrentUserContext.get();
        if (current == null) {
            throw new ApiExceptions.UnauthenticatedException();
        }
        return current;
    }
}
