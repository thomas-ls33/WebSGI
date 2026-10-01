package fr.esgi.websgi.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordHasherTest {

    private final PasswordHasher passwordHasher = new PasswordHasher();

    @Test
    void shouldNotReturnRawPassword_whenHashing() {
        String hash = passwordHasher.hash("motdepasse123");

        assertThat(hash).isNotEqualTo("motdepasse123");
    }

    @Test
    void shouldProduceDifferentHashes_whenHashingSamePasswordTwice() {
        String firstHash = passwordHasher.hash("motdepasse123");
        String secondHash = passwordHasher.hash("motdepasse123");

        assertThat(firstHash).isNotEqualTo(secondHash);
    }

    @Test
    void shouldMatch_whenPasswordIsCorrect() {
        String hash = passwordHasher.hash("motdepasse123");

        assertThat(passwordHasher.matches("motdepasse123", hash)).isTrue();
    }

    @Test
    void shouldNotMatch_whenPasswordIsWrong() {
        String hash = passwordHasher.hash("motdepasse123");

        assertThat(passwordHasher.matches("autre-mot-de-passe", hash)).isFalse();
    }
}
