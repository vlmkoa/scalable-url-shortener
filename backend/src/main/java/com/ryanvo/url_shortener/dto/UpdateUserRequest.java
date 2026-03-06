package com.ryanvo.url_shortener.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String role;
    private String status;
}