package com.example.webapplication.dto.jwt;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Nur nicht-null Felder im JSON
public class JwtResponse {
    
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @JsonProperty("token_type")
    @Builder.Default
    private String tokenType = "Bearer";
    
    @JsonProperty("expires_in")
    private Long expiresIn; // Sekunden
    
    @JsonProperty("issued_at")
    private Instant issuedAt;
    
    @JsonProperty("expires_at")
    private Instant expiresAt;
    
    // User Information
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    
    @JsonProperty("roles")
    private List<String> roles;
    
    @JsonProperty("permissions")
    private List<String> permissions;
    
    // Einfacher Konstruktor
    public JwtResponse(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }
    
    // Konstruktor mit Access und Refresh Token
    public JwtResponse(String accessToken, String refreshToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.issuedAt = Instant.now();
        this.expiresAt = Instant.now().plusSeconds(expiresIn);
    }
}

