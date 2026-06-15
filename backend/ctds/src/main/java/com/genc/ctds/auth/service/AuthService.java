package com.genc.ctds.auth.service;

import com.genc.ctds.auth.model.RoleType;
import com.genc.ctds.auth.model.User;
import com.genc.ctds.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserActivityService userActivityService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       UserActivityService userActivityService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userActivityService = userActivityService;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveUser(User user, String actor) {
        String normalizedUsername = user.getUsername().trim();
        Optional<User> existingUser = userRepository.findByUsername(normalizedUsername);

        User userToSave = existingUser.orElseGet(User::new);
        userToSave.setUsername(normalizedUsername);
        userToSave.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        userToSave.setRole(user.getRole());

        if (existingUser.isPresent()) {
            userToSave.setActive(existingUser.get().isActive());
        } else {
            userToSave.setActive(true);
        }

        User savedUser = userRepository.save(userToSave);
        if (existingUser.isEmpty()) {
            userActivityService.logUserCreated(savedUser, actor);
        }
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Map<RoleType, Long> getRoleDistribution() {
        Map<RoleType, Long> roleDistribution = new LinkedHashMap<>();
        for (RoleType role : RoleType.values()) {
            roleDistribution.put(role, 0L);
        }

        for (Object[] row : userRepository.countUsersByRole()) {
            RoleType role = (RoleType) row[0];
            Long count = (Long) row[1];
            roleDistribution.put(role, count);
        }
        return roleDistribution;
    }

    public boolean hasUser(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .roles(user.getRole().name())
                .disabled(!user.isActive())
                .build();
    }

}

