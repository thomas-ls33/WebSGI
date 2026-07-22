package fr.esgi.websgi.dto;

import fr.esgi.websgi.entity.User;

public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String role;

    public static UserResponse from(User user) {
        UserResponse dto = new UserResponse();
        dto.id = user.getId();
        dto.fullName = user.getFullName();
        dto.email = user.getEmail();
        dto.role = user.getRole().name().toLowerCase();
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

    public String getRole() {
        return role;
    }
}
