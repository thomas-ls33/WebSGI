package fr.esgi.websgi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String EMAIL = "alice@example.com";

    @Test
    void shouldReturnTokenAndUserRole_whenRegistering() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("fullName", "Alice Martin", "email", EMAIL, "password", PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value(EMAIL))
                .andExpect(jsonPath("$.user.role").value("user"));
    }

    @Test
    void shouldReturnConflict_whenRegisteringWithEmailAlreadyUsed() throws Exception {
        registerAndGetToken(EMAIL);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("fullName", "Autre Alice", "email", EMAIL.toUpperCase(), "password", PASSWORD))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequest_whenRegisteringWithTooShortPassword() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("fullName", "Alice Martin", "email", EMAIL, "password", "court"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Le mot de passe doit contenir au moins 8 caractères"));
    }

    @Test
    void shouldReturnBadRequest_whenRegisteringWithInvalidEmail() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("fullName", "Alice Martin", "email", "pas-un-email", "password", PASSWORD))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnToken_whenLoginCredentialsAreValid() throws Exception {
        registerAndGetToken(EMAIL);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", EMAIL, "password", PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void shouldReturnUnauthorized_whenLoginPasswordIsWrong() throws Exception {
        registerAndGetToken(EMAIL);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", EMAIL, "password", "mauvais-mot-de-passe"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorized_whenLoginEmailIsUnknown() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", "inconnu@example.com", "password", PASSWORD))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldLoginAsAdmin_whenUsingSeededAdminAccount() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", ADMIN_EMAIL, "password", ADMIN_PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.role").value("admin"));
    }

    @Test
    void shouldReturnCurrentUser_whenTokenIsValid() throws Exception {
        String token = registerAndGetToken(EMAIL);

        mockMvc.perform(get("/auth/me").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.role").value("user"));
    }

    @Test
    void shouldReturnUnauthorized_whenMeIsCalledWithoutToken() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorized_whenMeIsCalledWithInvalidToken() throws Exception {
        mockMvc.perform(get("/auth/me").header("Authorization", bearer("token-invalide")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAnswerOkWithoutCreatingToken_whenForgotPasswordEmailIsUnknown() throws Exception {
        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", "inconnu@example.com"))))
                .andExpect(status().isOk());

        Integer tokenCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM password_reset_tokens", Integer.class);
        assertThat(tokenCount).isZero();
    }

    @Test
    void shouldChangePassword_whenResetTokenIsValid() throws Exception {
        registerAndGetToken(EMAIL);
        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", EMAIL))))
                .andExpect(status().isOk());
        String resetToken = jdbcTemplate.queryForObject("SELECT token FROM password_reset_tokens", String.class);

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("token", resetToken, "password", "nouveau-mdp-123"))))
                .andExpect(status().isOk());

        loginAndGetToken(EMAIL, "nouveau-mdp-123");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", EMAIL, "password", PASSWORD))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequest_whenResetTokenIsUsedTwice() throws Exception {
        registerAndGetToken(EMAIL);
        mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("email", EMAIL))));
        String resetToken = jdbcTemplate.queryForObject("SELECT token FROM password_reset_tokens", String.class);
        String resetBody = json(Map.of("token", resetToken, "password", "nouveau-mdp-123"));
        mockMvc.perform(post("/auth/reset-password").contentType(MediaType.APPLICATION_JSON).content(resetBody))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/reset-password").contentType(MediaType.APPLICATION_JSON).content(resetBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_whenResetTokenIsUnknown() throws Exception {
        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("token", "token-inconnu", "password", "nouveau-mdp-123"))))
                .andExpect(status().isBadRequest());
    }
}
