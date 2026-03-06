package com.ryanvo.url_shortener.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
}