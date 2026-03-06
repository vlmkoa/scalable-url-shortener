package com.ryanvo.url_shortener.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ryanvo.url_shortener.dto.ShortenRequest;
import com.ryanvo.url_shortener.model.Role;
import com.ryanvo.url_shortener.service.UrlService;

import jakarta.validation.Valid;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<?> shorten(@Valid @RequestBody ShortenRequest request,
                                     Authentication authentication) {
        try {
            Role role = Role.FREE;
            if (authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                role = Role.ADMIN;
            } else if (authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_PREMIUM"))) {
                role = Role.PREMIUM;
            }

            String shortCode = urlService.shortenUrl(
                    request.getOriginalUrl(),
                    request.getCustomAlias(),
                    role);
            return ResponseEntity.ok(shortCode);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode) {
        return doRedirect(shortCode);
    }

    /** Redirect via /r/{shortCode} so nginx can route without regex (e.g. /r/abc12). */
    @GetMapping("/r/{shortCode}")
    public ResponseEntity<?> redirectWithPrefix(@PathVariable String shortCode) {
        return doRedirect(shortCode);
    }

    private ResponseEntity<?> doRedirect(String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);

        if (originalUrl == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleAliasConflict(IllegalArgumentException ex) {
        return ResponseEntity.status(409).body(ex.getMessage());
    }
}