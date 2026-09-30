package fr.esgi.websgi.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestStatusTest {

    @ParameterizedTest
    @CsvSource({
            "En attente, EN_ATTENTE",
            "En cours de traitement, EN_COURS",
            "Validée, VALIDEE",
            "Refusée, REFUSEE"
    })
    void shouldResolveStatus_whenLabelIsGiven(String label, RequestStatus expected) {
        assertThat(RequestStatus.fromLabel(label)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "EN_ATTENTE, EN_ATTENTE",
            "en_cours, EN_COURS",
            "validee, VALIDEE"
    })
    void shouldResolveStatus_whenNameIsGivenIgnoringCase(String name, RequestStatus expected) {
        assertThat(RequestStatus.fromLabel(name)).isEqualTo(expected);
    }

    @Test
    void shouldThrow_whenLabelIsUnknown() {
        assertThatThrownBy(() -> RequestStatus.fromLabel("Inconnu"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Inconnu");
    }
}
