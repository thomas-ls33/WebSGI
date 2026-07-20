package fr.esgi.websgi.repository;

import fr.esgi.websgi.entity.HostingRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HostingRequestRepository extends JpaRepository<HostingRequest, Long> {

    List<HostingRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<HostingRequest> findAllByOrderByCreatedAtDesc();
}
