package com.ryanvo.url_shortener.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}