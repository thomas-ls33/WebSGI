package fr.esgi.websgi.controller;

import fr.esgi.websgi.dto.HostingRequestResponse;
import fr.esgi.websgi.dto.StatusUpdateRequest;
import fr.esgi.websgi.exception.ApiExceptions;
import fr.esgi.websgi.security.AuthenticatedUser;
import fr.esgi.websgi.security.CurrentUserContext;
import fr.esgi.websgi.service.HostingRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/requests")
public class AdminRequestController {

    private final HostingRequestService hostingRequestService;

    public AdminRequestController(HostingRequestService hostingRequestService) {
        this.hostingRequestService = hostingRequestService;
    }

    @GetMapping
    public ResponseEntity<List<HostingRequestResponse>> all() {
        requireAdmin();
        return ResponseEntity.ok(hostingRequestService.findAll());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<HostingRequestResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request
    ) {
        requireAdmin();
        return ResponseEntity.ok(hostingRequestService.updateStatus(id, request.getStatus()));
    }

    private void requireAdmin() {
        AuthenticatedUser current = CurrentUserContext.get();
        if (current == null) {
            throw new ApiExceptions.UnauthenticatedException();
        }
        if (!current.isAdmin()) {
            throw new ApiExceptions.ForbiddenException();
        }
    }
}
