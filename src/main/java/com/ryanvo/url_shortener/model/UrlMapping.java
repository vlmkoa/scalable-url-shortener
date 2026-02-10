package com.ryanvo.url_shortener.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "url_mapping", indexes = {@Index(name = "idx_short_code", columnList = "shortCode", unique = true)})
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String longUrl;

    @Column(nullable = false, unique = true, length = 20)
    private String shortCode;

    private Instant createdDate = Instant.now();
}