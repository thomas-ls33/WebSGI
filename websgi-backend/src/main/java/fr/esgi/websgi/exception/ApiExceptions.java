package fr.esgi.websgi.exception;

public class ApiExceptions {

    public static class EmailAlreadyUsedException extends RuntimeException {
        public EmailAlreadyUsedException(String email) {
            super("Un compte existe déjà avec l'email " + email);
        }
    }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() {
            super("Email ou mot de passe incorrect");
        }
    }

    public static class UnauthenticatedException extends RuntimeException {
        public UnauthenticatedException() {
            super("Authentification requise");
        }
    }

    public static class ForbiddenException extends RuntimeException {
        public ForbiddenException() {
            super("Accès refusé");
        }
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidResetTokenException extends RuntimeException {
        public InvalidResetTokenException() {
            super("Lien de réinitialisation invalide ou expiré");
        }
    }
}
