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
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
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
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
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

