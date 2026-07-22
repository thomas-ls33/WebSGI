package fr.esgi.websgi.controller;

import fr.esgi.websgi.dto.HostingRequestCreateDto;
import fr.esgi.websgi.dto.HostingRequestResponse;
import fr.esgi.websgi.exception.ApiExceptions;
import fr.esgi.websgi.security.AuthenticatedUser;
import fr.esgi.websgi.security.CurrentUserContext;
import fr.esgi.websgi.service.HostingRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class HostingRequestController {

    private final HostingRequestService hostingRequestService;

    public HostingRequestController(HostingRequestService hostingRequestService) {
        this.hostingRequestService = hostingRequestService;
    }

    @PostMapping
    public ResponseEntity<HostingRequestResponse> create(@Valid @RequestBody HostingRequestCreateDto dto) {
        AuthenticatedUser current = requireAuthenticated();
        return ResponseEntity.ok(hostingRequestService.create(current.getId(), dto));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<HostingRequestResponse>> mine() {
        AuthenticatedUser current = requireAuthenticated();
        return ResponseEntity.ok(hostingRequestService.findMine(current.getId()));
    }

    private AuthenticatedUser requireAuthenticated() {
        AuthenticatedUser current = CurrentUserContext.get();
        if (current == null) {
            throw new ApiExceptions.UnauthenticatedException();
        }
        return current;
    }
}
