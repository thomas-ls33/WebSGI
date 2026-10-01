package fr.esgi.websgi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminRequestControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String ALICE_EMAIL = "alice@example.com";
    private static final String BOB_EMAIL = "bob@example.com";

    @Test
    void shouldReturnUnauthorized_whenListingAllWithoutToken() throws Exception {
        mockMvc.perform(get("/admin/requests"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbidden_whenListingAllAsSimpleUser() throws Exception {
        String token = registerAndGetToken(ALICE_EMAIL);

        mockMvc.perform(get("/admin/requests").header("Authorization", bearer(token)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnRequestsOfAllUsers_whenListingAllAsAdmin() throws Exception {
        createHostingRequest(registerAndGetToken(ALICE_EMAIL), "Site d'Alice");
        createHostingRequest(registerAndGetToken(BOB_EMAIL), "Site de Bob");

        mockMvc.perform(get("/admin/requests").header("Authorization", bearer(loginAsAdmin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldReturnForbidden_whenUpdatingStatusAsSimpleUser() throws Exception {
        String token = registerAndGetToken(ALICE_EMAIL);
        long requestId = createHostingRequest(token, "Site d'Alice");

        mockMvc.perform(patch("/admin/requests/{id}/status", requestId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "Validée"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateStatusVisibleByOwner_whenAdminValidatesRequest() throws Exception {
        String aliceToken = registerAndGetToken(ALICE_EMAIL);
        long requestId = createHostingRequest(aliceToken, "Site d'Alice");

        mockMvc.perform(patch("/admin/requests/{id}/status", requestId)
                        .header("Authorization", bearer(loginAsAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "Validée"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Validée"));

        mockMvc.perform(get("/requests/mine").header("Authorization", bearer(aliceToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Validée"));
    }

    @Test
    void shouldReturnNotFound_whenUpdatingStatusOfUnknownRequest() throws Exception {
        mockMvc.perform(patch("/admin/requests/{id}/status", 999999)
                        .header("Authorization", bearer(loginAsAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "Validée"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_whenUpdatingWithUnknownStatus() throws Exception {
        long requestId = createHostingRequest(registerAndGetToken(ALICE_EMAIL), "Site d'Alice");

        mockMvc.perform(patch("/admin/requests/{id}/status", requestId)
                        .header("Authorization", bearer(loginAsAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "Inconnu"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_whenUpdatingWithBlankStatus() throws Exception {
        long requestId = createHostingRequest(registerAndGetToken(ALICE_EMAIL), "Site d'Alice");

        mockMvc.perform(patch("/admin/requests/{id}/status", requestId)
                        .header("Authorization", bearer(loginAsAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", ""))))
                .andExpect(status().isBadRequest());
    }
}
