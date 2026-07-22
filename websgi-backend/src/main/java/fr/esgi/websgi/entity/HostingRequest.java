package fr.esgi.websgi.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hosting_requests")
public class HostingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, length = 180)
    private String email;

    @Column(name = "project_type", nullable = false, length = 30)
    private String projectType;

    @Column(name = "project_name", nullable = false, length = 150)
    private String projectName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "hosting_type", nullable = false, length = 150)
    private String hostingType;

    @Column(name = "needs_database", nullable = false)
    private boolean needsDatabase;

    @Column(name = "database_type", nullable = false, length = 30)
    private String databaseType = "aucune";

    @Column(name = "git_repo", length = 300)
    private String gitRepo;

    @Column(name = "expected_traffic", length = 150)
    private String expectedTraffic;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RequestStatus status = RequestStatus.EN_ATTENTE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHostingType() {
        return hostingType;
    }

    public void setHostingType(String hostingType) {
        this.hostingType = hostingType;
    }

    public boolean isNeedsDatabase() {
        return needsDatabase;
    }

    public void setNeedsDatabase(boolean needsDatabase) {
        this.needsDatabase = needsDatabase;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getGitRepo() {
        return gitRepo;
    }

    public void setGitRepo(String gitRepo) {
        this.gitRepo = gitRepo;
    }

    public String getExpectedTraffic() {
        return expectedTraffic;
    }

    public void setExpectedTraffic(String expectedTraffic) {
        this.expectedTraffic = expectedTraffic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
