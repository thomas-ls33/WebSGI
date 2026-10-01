package fr.esgi.websgi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "cle-de-test-suffisamment-longue-pour-hmac-sha256";
    private static final String OTHER_SECRET = "une-autre-cle-de-test-suffisamment-longue-hmac";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 60);
    }

    @Test
    void shouldExtractUserIdAndRole_whenTokenIsGenerated() {
        String token = jwtService.generateToken(42L, "ADMIN");

        assertThat(jwtService.extractUserId(token)).isEqualTo(42L);
        assertThat(jwtService.extractRole(token)).isEqualTo("ADMIN");
    }

    @Test
    void shouldBeValid_whenTokenIsFreshlyGenerated() {
        String token = jwtService.generateToken(1L, "USER");

        assertThat(jwtService.isValid(token)).isTrue();
    }

    @Test
    void shouldBeInvalid_whenTokenIsExpired() {
        JwtService expiredJwtService = new JwtService(SECRET, -1);
        String token = expiredJwtService.generateToken(1L, "USER");

        assertThat(jwtService.isValid(token)).isFalse();
    }

    @Test
    void shouldBeInvalid_whenTokenIsSignedWithAnotherSecret() {
        JwtService otherJwtService = new JwtService(OTHER_SECRET, 60);
        String token = otherJwtService.generateToken(1L, "USER");

        assertThat(jwtService.isValid(token)).isFalse();
    }

    @Test
    void shouldBeInvalid_whenPayloadIsTampered() {
        String[] userToken = jwtService.generateToken(1L, "USER").split("\\.");
        String[] adminToken = jwtService.generateToken(2L, "ADMIN").split("\\.");
        String tamperedToken = userToken[0] + "." + adminToken[1] + "." + userToken[2];

        assertThat(jwtService.isValid(tamperedToken)).isFalse();
    }

    @Test
    void shouldBeInvalid_whenTokenIsMalformed() {
        assertThat(jwtService.isValid("ceci-n-est-pas-un-jwt")).isFalse();
    }

    @Test
    void shouldBeInvalid_whenTokenIsBlank() {
        assertThat(jwtService.isValid("")).isFalse();
    }
}
