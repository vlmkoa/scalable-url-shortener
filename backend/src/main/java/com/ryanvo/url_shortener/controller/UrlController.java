package com.ryanvo.url_shortener.controller;

import com.ryanvo.url_shortener.dto.ShortenRequest;
import com.ryanvo.url_shortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import java.net.URI;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<?> shorten(@Valid @RequestBody ShortenRequest request) {
        try {
            String shortCode = urlService.shortenUrl(request.getOriginalUrl(), request.getCustomAlias());
            return ResponseEntity.ok(shortCode);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode) {
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