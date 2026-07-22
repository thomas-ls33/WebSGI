package fr.esgi.websgi.dto;

import fr.esgi.websgi.entity.HostingRequest;

import java.time.LocalDateTime;

public class HostingRequestResponse {

    private Long id;
    private String fullName;
    private String email;
    private String projectType;
    private String projectName;
    private String description;
    private String hostingType;
    private String needsDatabase;
    private String databaseType;
    private String gitRepo;
    private String expectedTraffic;
    private String message;
    private String status;
    private LocalDateTime createdAt;

    public static HostingRequestResponse from(HostingRequest entity) {
        HostingRequestResponse dto = new HostingRequestResponse();
        dto.id = entity.getId();
        dto.fullName = entity.getFullName();
        dto.email = entity.getEmail();
        dto.projectType = entity.getProjectType();
        dto.projectName = entity.getProjectName();
        dto.description = entity.getDescription();
        dto.hostingType = entity.getHostingType();
        dto.needsDatabase = entity.isNeedsDatabase() ? "oui" : "non";
        dto.databaseType = entity.getDatabaseType();
        dto.gitRepo = entity.getGitRepo();
        dto.expectedTraffic = entity.getExpectedTraffic();
        dto.message = entity.getMessage();
        dto.status = entity.getStatus().getLabel();
        dto.createdAt = entity.getCreatedAt();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getProjectType() {
        return projectType;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getDescription() {
        return description;
    }

    public String getHostingType() {
        return hostingType;
    }

    public String getNeedsDatabase() {
        return needsDatabase;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public String getGitRepo() {
        return gitRepo;
    }

    public String getExpectedTraffic() {
        return expectedTraffic;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
