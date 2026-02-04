package com.ryanvo.url_shortener.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "url_mapping")
public class UrlMapping {

    @Id
    private Long id; // The Snowflake ID

    @Column(nullable = false, columnDefinition = "TEXT")
    private String longUrl;

    @Column(nullable = false, length = 10) // Optimization: Limit char width
    private String shortCode;

    private Instant createdDate = Instant.now();
}