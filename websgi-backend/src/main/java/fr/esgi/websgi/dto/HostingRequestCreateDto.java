package fr.esgi.websgi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class HostingRequestCreateDto {

    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String projectType;

    @NotBlank
    private String projectName;

    @NotBlank
    private String description;

    @NotBlank
    private String hostingType;

    @NotBlank
    private String needsDatabase;

    private String databaseType;

    private String gitRepo;

    private String expectedTraffic;

    private String message;

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

    public String getNeedsDatabase() {
        return needsDatabase;
    }

    public void setNeedsDatabase(String needsDatabase) {
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
}
