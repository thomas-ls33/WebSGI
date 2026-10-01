package fr.esgi.websgi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HostingRequestControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String ALICE_EMAIL = "alice@example.com";
    private static final String BOB_EMAIL = "bob@example.com";

    @Test
    void shouldReturnUnauthorized_whenCreatingWithoutToken() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(hostingRequestBody("Mon site", "non", "aucune"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreatePendingRequest_whenAuthenticated() throws Exception {
        String token = registerAndGetToken(ALICE_EMAIL);

        mockMvc.perform(post("/requests")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(hostingRequestBody("Mon site", "oui", "PostgreSQL"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.projectName").value("Mon site"))
                .andExpect(jsonPath("$.status").value("En attente"))
                .andExpect(jsonPath("$.needsDatabase").value("oui"))
                .andExpect(jsonPath("$.databaseType").value("PostgreSQL"));
    }

    @Test
    void shouldStoreNoDatabaseType_whenProjectDoesNotNeedDatabase() throws Exception {
        String token = registerAndGetToken(ALICE_EMAIL);

        mockMvc.perform(post("/requests")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(hostingRequestBody("Mon site", "non", "PostgreSQL"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.needsDatabase").value("non"))
                .andExpect(jsonPath("$.databaseType").value("aucune"));
    }

    @Test
    void shouldReturnBadRequest_whenRequiredFieldIsMissing() throws Exception {
        String token = registerAndGetToken(ALICE_EMAIL);

        mockMvc.perform(post("/requests")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("fullName", "Alice Martin"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUnauthorized_whenListingMineWithoutToken() throws Exception {
        mockMvc.perform(get("/requests/mine"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnOnlyOwnRequests_whenListingMine() throws Exception {
        String aliceToken = registerAndGetToken(ALICE_EMAIL);
        String bobToken = registerAndGetToken(BOB_EMAIL);
        createHostingRequest(aliceToken, "Site d'Alice");
        createHostingRequest(bobToken, "Site de Bob");
        createHostingRequest(bobToken, "API de Bob");

        mockMvc.perform(get("/requests/mine").header("Authorization", bearer(aliceToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].projectName").value("Site d'Alice"));

        mockMvc.perform(get("/requests/mine").header("Authorization", bearer(bobToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
