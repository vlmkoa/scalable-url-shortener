package com.ryanvo.url_shortener.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class ShortenRequest {
    @NotNull
    private String originalUrl;
    @Size(min = 5, max = 20)
    private String customAlias;
}
