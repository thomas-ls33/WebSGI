package fr.esgi.websgi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base des tests d'intégration : démarre l'application complète sur la base PostgreSQL de test
 * (migrations Flyway incluses) et remet la base dans un état connu avant chaque test.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class AbstractIntegrationTest {

    protected static final String PASSWORD = "motdepasse123";
    protected static final String ADMIN_EMAIL = "admin@websgi.fr";
    protected static final String ADMIN_PASSWORD = "admin123";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM password_reset_tokens");
        jdbcTemplate.update("DELETE FROM hosting_requests");
        jdbcTemplate.update("DELETE FROM users WHERE email <> ?", ADMIN_EMAIL);
    }

    protected String json(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    protected String bearer(String token) {
        return "Bearer " + token;
    }

    protected String registerAndGetToken(String email) throws Exception {
        Map<String, String> body = Map.of(
                "fullName", "Utilisateur Test",
                "email", email,
                "password", PASSWORD
        );
        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isOk())
                .andReturn();
        return readField(result, "token");
    }

    protected String loginAndGetToken(String email, String password) throws Exception {
        Map<String, String> body = Map.of("email", email, "password", password);
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isOk())
                .andReturn();
        return readField(result, "token");
    }

    protected String loginAsAdmin() throws Exception {
        return loginAndGetToken(ADMIN_EMAIL, ADMIN_PASSWORD);
    }

    protected Map<String, String> hostingRequestBody(String projectName, String needsDatabase, String databaseType) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("fullName", "Utilisateur Test");
        body.put("email", "utilisateur@example.com");
        body.put("projectType", "site-web");
        body.put("projectName", projectName);
        body.put("description", "Un projet de test");
        body.put("hostingType", "mutualisé");
        body.put("needsDatabase", needsDatabase);
        body.put("databaseType", databaseType);
        return body;
    }

    protected long createHostingRequest(String token, String projectName) throws Exception {
        MvcResult result = mockMvc.perform(post("/requests")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(hostingRequestBody(projectName, "non", "aucune"))))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .get("id")
                .asLong();
    }

    private String readField(MvcResult result, String field) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .get(field)
                .asText();
    }
}
