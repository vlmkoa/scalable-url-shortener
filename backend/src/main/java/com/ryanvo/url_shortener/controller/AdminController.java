package com.ryanvo.url_shortener.controller;

import java.util.List;

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
        User user = users.findById(id).orElseThrow();
        if (req.getRole() != null) {
            user.setRole(Role.valueOf(req.getRole()));
        }
        if (req.getStatus() != null) {
            user.setStatus(req.getStatus());
        }
        users.save(user);
        return ResponseEntity.ok().build();
    }
}