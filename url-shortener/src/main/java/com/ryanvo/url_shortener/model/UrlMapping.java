package com.ryanvo.url_shortener.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
@Table(name = "url_mapping")
public class UrlMapping {

    @Id
    private Long id; // The Snowflake ID

    @Column(nullable = false, columnDefinition = "TEXT")
    private String longUrl;

    @Column(nullable = false, length = 10) // Optimization: Limit char width
    private String shortCode;

    private LocalDateTime createdDate;

    // Default constructor required by JPA
    public UrlMapping() {}

    public UrlMapping(Long id, String longUrl, String shortCode) {
        this.id = id;
        this.longUrl = longUrl;
        this.shortCode = shortCode;
        this.createdDate = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getLongUrl() { return longUrl; }
    public String getShortCode() { return shortCode; }
}