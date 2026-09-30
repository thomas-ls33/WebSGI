package fr.esgi.websgi.service;

import fr.esgi.websgi.dto.HostingRequestCreateDto;
import fr.esgi.websgi.dto.HostingRequestResponse;
import fr.esgi.websgi.entity.HostingRequest;
import fr.esgi.websgi.entity.RequestStatus;
import fr.esgi.websgi.entity.Role;
import fr.esgi.websgi.entity.User;
import fr.esgi.websgi.exception.ApiExceptions;
import fr.esgi.websgi.repository.HostingRequestRepository;
import fr.esgi.websgi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HostingRequestServiceTest {

    private static final Long USER_ID = 1L;

    @Mock
    private HostingRequestRepository hostingRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HostingRequestService hostingRequestService;

    @Test
    void shouldCreatePendingRequest_whenUserExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user()));

        HostingRequestResponse response = hostingRequestService.create(USER_ID, createDto("oui", "PostgreSQL"));

        ArgumentCaptor<HostingRequest> captor = ArgumentCaptor.forClass(HostingRequest.class);
        verify(hostingRequestRepository).save(captor.capture());
        HostingRequest saved = captor.getValue();
        assertThat(saved.getUser().getId()).isEqualTo(USER_ID);
        assertThat(saved.getProjectName()).isEqualTo("Mon site");
        assertThat(saved.getStatus()).isEqualTo(RequestStatus.EN_ATTENTE);
        assertThat(response.getStatus()).isEqualTo("En attente");
    }

    @Test
    void shouldKeepDatabaseType_whenProjectNeedsDatabase() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user()));

        HostingRequestResponse response = hostingRequestService.create(USER_ID, createDto("oui", "PostgreSQL"));

        assertThat(response.getNeedsDatabase()).isEqualTo("oui");
        assertThat(response.getDatabaseType()).isEqualTo("PostgreSQL");
    }

    @Test
    void shouldIgnoreCaseOfNeedsDatabase_whenCreating() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user()));

        HostingRequestResponse response = hostingRequestService.create(USER_ID, createDto("OUI", "MySQL"));

        assertThat(response.getNeedsDatabase()).isEqualTo("oui");
        assertThat(response.getDatabaseType()).isEqualTo("MySQL");
    }

    @Test
    void shouldForceDatabaseTypeToNone_whenProjectDoesNotNeedDatabase() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user()));

        HostingRequestResponse response = hostingRequestService.create(USER_ID, createDto("non", "PostgreSQL"));

        assertThat(response.getNeedsDatabase()).isEqualTo("non");
        assertThat(response.getDatabaseType()).isEqualTo("aucune");
    }

    @Test
    void shouldThrow_whenCreatingWithUnknownUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hostingRequestService.create(USER_ID, createDto("non", null)))
                .isInstanceOf(ApiExceptions.UnauthenticatedException.class);
        verify(hostingRequestRepository, never()).save(any());
    }

    @Test
    void shouldReturnOwnRequests_whenFindingMine() {
        when(hostingRequestRepository.findByUserIdOrderByCreatedAtDesc(USER_ID))
                .thenReturn(List.of(hostingRequest(10L), hostingRequest(11L)));

        List<HostingRequestResponse> responses = hostingRequestService.findMine(USER_ID);

        assertThat(responses).extracting(HostingRequestResponse::getId).containsExactly(10L, 11L);
    }

    @Test
    void shouldReturnAllRequests_whenFindingAll() {
        when(hostingRequestRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(hostingRequest(10L), hostingRequest(11L), hostingRequest(12L)));

        List<HostingRequestResponse> responses = hostingRequestService.findAll();

        assertThat(responses).hasSize(3);
    }

    @Test
    void shouldUpdateStatus_whenRequestExists() {
        HostingRequest entity = hostingRequest(10L);
        when(hostingRequestRepository.findById(10L)).thenReturn(Optional.of(entity));

        HostingRequestResponse response = hostingRequestService.updateStatus(10L, "Validée");

        assertThat(entity.getStatus()).isEqualTo(RequestStatus.VALIDEE);
        assertThat(response.getStatus()).isEqualTo("Validée");
        verify(hostingRequestRepository).save(entity);
    }

    @Test
    void shouldThrow_whenUpdatingStatusOfUnknownRequest() {
        when(hostingRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hostingRequestService.updateStatus(99L, "Validée"))
                .isInstanceOf(ApiExceptions.NotFoundException.class)
                .hasMessage("Demande introuvable");
    }

    @Test
    void shouldThrow_whenUpdatingWithUnknownStatusLabel() {
        HostingRequest entity = hostingRequest(10L);
        when(hostingRequestRepository.findById(10L)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> hostingRequestService.updateStatus(10L, "Inconnu"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(entity.getStatus()).isEqualTo(RequestStatus.EN_ATTENTE);
        verify(hostingRequestRepository, never()).save(any());
    }

    private User user() {
        User user = new User();
        user.setId(USER_ID);
        user.setFullName("Alice Martin");
        user.setEmail("alice@example.com");
        user.setRole(Role.USER);
        return user;
    }

    private HostingRequestCreateDto createDto(String needsDatabase, String databaseType) {
        HostingRequestCreateDto dto = new HostingRequestCreateDto();
        dto.setFullName("Alice Martin");
        dto.setEmail("alice@example.com");
        dto.setProjectType("site-web");
        dto.setProjectName("Mon site");
        dto.setDescription("Un site vitrine");
        dto.setHostingType("mutualisé");
        dto.setNeedsDatabase(needsDatabase);
        dto.setDatabaseType(databaseType);
        return dto;
    }

    private HostingRequest hostingRequest(Long id) {
        HostingRequest entity = new HostingRequest();
        entity.setId(id);
        entity.setUser(user());
        entity.setFullName("Alice Martin");
        entity.setEmail("alice@example.com");
        entity.setProjectType("site-web");
        entity.setProjectName("Mon site");
        entity.setDescription("Un site vitrine");
        entity.setHostingType("mutualisé");
        return entity;
    }
}
