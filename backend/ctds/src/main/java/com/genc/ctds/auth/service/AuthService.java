package com.genc.ctds.auth.service;

import com.genc.ctds.auth.model.RoleType;
import com.genc.ctds.auth.model.User;
import com.genc.ctds.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserActivityService userActivityService;

    public AuthService(UserRepository userRepository, UserActivityService userActivityService) {
        this.userRepository = userRepository;
        this.userActivityService = userActivityService;
    }

    public Optional<User> authenticate(String username, String rawPassword) {
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            return Optional.empty();
        }

        Optional<User> userOptional = userRepository.findByUsername(username.trim());
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();
//        if (!user.isActive()) {
//            return Optional.empty();
//        }

        if (!rawPassword.equals(user.getPasswordHash())) {
            return Optional.empty();
        }

        return Optional.of(user);
    }
    public void saveUser(User user, String actor) {
        String normalizedUsername = user.getUsername().trim();
        Optional<User> existingUser = userRepository.findByUsername(normalizedUsername);

        User userToSave = existingUser.orElseGet(User::new);
        userToSave.setUsername(normalizedUsername);
        userToSave.setPasswordHash(user.getPasswordHash());
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

//    public User createUser(String username, String rawPassword, RoleType role, boolean active) {
//        User user = new User();
//        user.setUsername(username.trim());
//        user.setPasswordHash(rawPassword);
//        user.setRole(role);
//        user.setActive(active);
//        return userRepository.save(user);
//    }
//
//    public User createOrUpdateUser(String username, String rawPassword, RoleType role, boolean active) {
//        Optional<User> existingUser = userRepository.findByUsername(username.trim());
//        User user = existingUser.orElseGet(User::new);
//        user.setUsername(username.trim());
//        user.setPasswordHash(rawPassword);
//        user.setRole(role);
//        user.setActive(active);
//        return userRepository.save(user);
//    }

    public boolean hasUser(String username) {
        return userRepository.existsByUsername(username);
    }
}

