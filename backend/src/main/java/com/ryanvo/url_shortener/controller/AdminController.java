package com.ryanvo.url_shortener.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ryanvo.url_shortener.dto.UpdateUserRequest;
import com.ryanvo.url_shortener.model.Role;
import com.ryanvo.url_shortener.model.User;
import com.ryanvo.url_shortener.repository.UserRepository;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Set<String> ALLOWED_STATUSES = Set.of("ACTIVE", "SUSPENDED", "BANNED");

    private final UserRepository users;

    public AdminController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/users")
    public List<User> listUsers() {
        return users.findAll();
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest req) {
        User user = users.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (req.getRole() != null) {
            try {
                user.setRole(Role.valueOf(req.getRole().toUpperCase().trim()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid role. Must be one of: FREE, PREMIUM, ADMIN");
            }
        }
        if (req.getStatus() != null) {
            String status = req.getStatus().toUpperCase().trim();
            if (!ALLOWED_STATUSES.contains(status)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid status. Must be one of: ACTIVE, SUSPENDED, BANNED");
            }
            user.setStatus(status);
        }

        users.save(user);
        return ResponseEntity.ok().build();
    }
}