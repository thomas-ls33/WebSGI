package fr.esgi.websgi.service;

import fr.esgi.websgi.dto.HostingRequestCreateDto;
import fr.esgi.websgi.dto.HostingRequestResponse;
import fr.esgi.websgi.entity.HostingRequest;
import fr.esgi.websgi.entity.RequestStatus;
import fr.esgi.websgi.entity.User;
import fr.esgi.websgi.exception.ApiExceptions;
import fr.esgi.websgi.repository.HostingRequestRepository;
import fr.esgi.websgi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HostingRequestService {

    private final HostingRequestRepository hostingRequestRepository;
    private final UserRepository userRepository;

    public HostingRequestService(HostingRequestRepository hostingRequestRepository, UserRepository userRepository) {
        this.hostingRequestRepository = hostingRequestRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public HostingRequestResponse create(Long userId, HostingRequestCreateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(ApiExceptions.UnauthenticatedException::new);

        HostingRequest entity = new HostingRequest();
        entity.setUser(user);
        entity.setFullName(dto.getFullName());
        entity.setEmail(dto.getEmail());
        entity.setProjectType(dto.getProjectType());
        entity.setProjectName(dto.getProjectName());
        entity.setDescription(dto.getDescription());
        entity.setHostingType(dto.getHostingType());
        entity.setNeedsDatabase("oui".equalsIgnoreCase(dto.getNeedsDatabase()));
        entity.setDatabaseType(entity.isNeedsDatabase() ? dto.getDatabaseType() : "aucune");
        entity.setGitRepo(dto.getGitRepo());
        entity.setExpectedTraffic(dto.getExpectedTraffic());
        entity.setMessage(dto.getMessage());
        entity.setStatus(RequestStatus.EN_ATTENTE);

        hostingRequestRepository.save(entity);
        return HostingRequestResponse.from(entity);
    }

    public List<HostingRequestResponse> findMine(Long userId) {
        return hostingRequestRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(HostingRequestResponse::from)
                .toList();
    }

    public List<HostingRequestResponse> findAll() {
        return hostingRequestRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(HostingRequestResponse::from)
                .toList();
    }

    @Transactional
    public HostingRequestResponse updateStatus(Long requestId, String statusLabel) {
        HostingRequest entity = hostingRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiExceptions.NotFoundException("Demande introuvable"));

        entity.setStatus(RequestStatus.fromLabel(statusLabel));
        hostingRequestRepository.save(entity);
        return HostingRequestResponse.from(entity);
    }
}
