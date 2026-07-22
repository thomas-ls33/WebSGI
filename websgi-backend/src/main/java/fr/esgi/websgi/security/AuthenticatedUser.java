package fr.esgi.websgi.security;

public class AuthenticatedUser {

    private final Long id;
    private final String role;

    public AuthenticatedUser(Long id, String role) {
        this.id = id;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}
