package com.example.webapplication.dto.jwt;

import lombok.Data;

@Data
public class JwtLoginRequest {
    private String username;
    private String password;
}

