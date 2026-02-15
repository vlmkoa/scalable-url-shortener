package com.ryanvo.url_shortener.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;


import lombok.Data;

@Data
public class ShortenRequest {
    @NotNull
    @Pattern(regexp = "^(http|https)://.*|[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}.*$",
            message = "Invalid URL format")
    private String originalUrl;
    @Size(max = 20, message = "Alias must be under 20 characters")
    private String customAlias;
}
