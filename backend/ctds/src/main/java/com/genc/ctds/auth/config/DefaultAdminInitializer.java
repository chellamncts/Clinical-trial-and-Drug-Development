package com.genc.ctds.auth.config;

import com.genc.ctds.auth.model.RoleType;
import com.genc.ctds.auth.model.User;
import com.genc.ctds.auth.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;
 
    @Value("${app.admin.password:admin@123}")
    private String adminPassword;

    public DefaultAdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initializeDefaultAdmin() {
        String username = adminUsername == null ? "" : adminUsername.trim();
        if (username.isEmpty() || adminPassword == null || adminPassword.isBlank()) {
            return;
        }

        // Create default admin only when it does not already exist.
        if (userRepository.existsByUsername(username)) {
            return;
        }

        User admin = new User();
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole(RoleType.ADMIN);
        admin.setActive(true);
        userRepository.save(admin);
    }
}

