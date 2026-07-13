package com.example.auth_service.config;

import com.example.auth_service.model.Role;
import com.example.auth_service.model.User;
import com.example.auth_service.repo.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (!userRepository.existsByUsername("Admin")) {
            User admin = new User();
            admin.setUsername("Admin");
            admin.setEmail("admin@ctddts.com");
            admin.setFullName("Administrator");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
    }
}