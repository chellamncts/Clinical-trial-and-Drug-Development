package com.example.auth_service.controller;

import com.example.auth_service.dto.UserRequest;
import com.example.auth_service.dto.UserResponse;
import com.example.auth_service.model.User;
import com.example.auth_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> listUsers(@RequestHeader(value = "X-User-Role", required = false) String role) {
        userService.requireAdmin(role);
        return userService.findAll();
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestBody UserRequest request) {
        userService.requireAdmin(role);
        return ResponseEntity.ok(userService.createUser(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable Long id) {
        userService.requireAdmin(role);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

