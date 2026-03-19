package com.example.webapplication.config.security.jwt;

import com.example.webapplication.config.security.properties.JwtProperties;
import com.example.webapplication.config.security.properties.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;


/**
 * Configuration class for JWT-related beans.
 * <p>
 * This class provides the necessary configuration to decode JSON Web Tokens (JWT)
 * using the properties defined in {@code JwtProperties}, such as the secret key.
 */
@Configuration
public class JwtConfig {

    private final JwtProperties jwtProperties;

    /**
     * Creates a new {@code JwtConfig} instance with the given JWT properties.
     *
     * @param jwtProperties the configuration properties containing the secret key and other JWT settings
     */
    public JwtConfig(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * Creates and configures a {@code JwtDecoder} bean.
     * <p>
     * The decoder is initialized with a secret key derived from the configured
     * JWT secret and uses the HmacSHA256 algorithm for verification.
     *
     * @return a configured {@code JwtDecoder} instance for validating JWT signatures
     * @throws IllegalArgumentException if the secret key is invalid or cannot be used
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        String secret = jwtProperties.getSecret();
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}
