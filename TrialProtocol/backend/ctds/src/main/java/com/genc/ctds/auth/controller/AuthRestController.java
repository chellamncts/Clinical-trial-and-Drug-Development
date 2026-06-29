package com.genc.ctds.auth.controller;

import com.genc.ctds.auth.model.User;
import com.genc.ctds.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST authentication endpoint for the standalone (fetch-based) frontend.
 * Returns the authenticated user's role so the JS can route to the right page.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthRestController {

    private final AuthService authService;

    public AuthRestController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = authService.authenticate(request.username(), request.password());
            return ResponseEntity.ok(Map.of(
                    "username", user.getUsername(),
                    "role", user.getRole().name()
            ));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /** Simple credentials payload. */
    public record LoginRequest(String username, String password) {
    }
}

