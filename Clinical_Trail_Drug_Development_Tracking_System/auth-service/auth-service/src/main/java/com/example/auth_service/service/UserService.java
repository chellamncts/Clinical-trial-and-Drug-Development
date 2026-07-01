package com.example.auth_service.service;

import com.example.auth_service.dto.UserRequest;
import com.example.auth_service.dto.UserResponse;
import com.example.auth_service.model.Role;
import com.example.auth_service.model.User;
import com.example.auth_service.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getFullName(), u.getEmail(), u.getRole().name()))
                .collect(Collectors.toList());
    }

    public UserResponse createUser(UserRequest req) {
        String username = req.getUsername() == null ? "" : req.getUsername().trim();
        String fullName = req.getFullName() == null ? "" : req.getFullName().trim();
        String email    = req.getEmail()    == null ? "" : req.getEmail().trim();
        String password = req.getPassword() == null ? "" : req.getPassword().trim();
        String role     = req.getRole()     == null ? "" : req.getRole().trim();

        if (username.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        if (password.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        if (role.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required");
        if (email.isEmpty() || !email.toLowerCase().endsWith("@ctddts.com"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Email is required and must use @ctddts.com (e.g. jsmith@ctddts.com)");

        if (userRepository.existsByUsername(username))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");

        User user = new User();
        user.setUsername(username);
        user.setFullName(fullName.isEmpty() ? null : fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.valueOf(role));
        userRepository.save(user);
        return new UserResponse(user.getId(), user.getUsername(), user.getFullName(), user.getEmail(), user.getRole().name());
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }
}

