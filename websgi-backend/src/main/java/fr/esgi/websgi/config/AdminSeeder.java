package fr.esgi.websgi.config;

import fr.esgi.websgi.entity.Role;
import fr.esgi.websgi.entity.User;
import fr.esgi.websgi.repository.UserRepository;
import fr.esgi.websgi.security.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final String seedEmail;
    private final String seedPassword;

    public AdminSeeder(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            @Value("${websgi.admin.seed-email}") String seedEmail,
            @Value("${websgi.admin.seed-password:}") String seedPassword
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.seedEmail = seedEmail;
        this.seedPassword = seedPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (seedPassword == null || seedPassword.isBlank()) {
            log.info("ADMIN_SEED_PASSWORD non défini : le compte administrateur n'est pas modifié");
            return;
        }
        if (seedPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalStateException(
                    "ADMIN_SEED_PASSWORD doit contenir au moins " + MIN_PASSWORD_LENGTH + " caractères");
        }

        User admin = userRepository.findByEmailIgnoreCase(seedEmail).orElseGet(this::newAdmin);
        admin.setRole(Role.ADMIN);
        if (admin.getPasswordHash() == null || !passwordHasher.matches(seedPassword, admin.getPasswordHash())) {
            admin.setPasswordHash(passwordHasher.hash(seedPassword));
            log.info("Mot de passe du compte administrateur {} défini depuis la configuration", seedEmail);
        }
        userRepository.save(admin);
    }

    private User newAdmin() {
        User admin = new User();
        admin.setFullName("Admin WebSGI");
        admin.setEmail(seedEmail);
        return admin;
    }
}
