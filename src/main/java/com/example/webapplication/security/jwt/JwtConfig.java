package com.example.webapplication.security.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Configuration class for JWT-based authentication in the application.
 * <p>
 * This class defines Spring beans required for decoding JWT tokens and
 * converting JWT claims into Spring Security authentication objects.
 *
 * <p><b>Responsibilities:</b>
 * <ul>
 *     <li>Configure a {@link JwtDecoder} for validating and parsing JWT tokens</li>
 *     <li>Configure a {@link JwtAuthenticationConverter} to map JWT claims to authorities</li>
 * </ul>
 *
 * <p><b>JWT Processing:</b>
 * <ul>
 *     <li>Tokens are verified using an HMAC SHA-256 secret key</li>
 *     <li>The secret is obtained from {@link JwtProperties}</li>
 *     <li>User roles are extracted from a custom claim defined in {@link JwtClaims}</li>
 * </ul>
 *
 * <p><b>Security Note:</b>
 * The secret key must match the one used for signing tokens. Any mismatch
 * will result in invalid token errors.
 */
@Configuration
public class JwtConfig {

    private final JwtProperties jwtProperties;

    /**
     * Creates a new {@code JwtConfig} instance.
     *
     * @param jwtProperties configuration properties containing the JWT secret and other settings
     */
    public JwtConfig(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * Creates a {@link JwtDecoder} bean used to validate and parse incoming JWT tokens.
     * <p>
     * The decoder uses the HMAC SHA-256 algorithm with a shared secret key.
     * The secret is converted into a {@link SecretKey} compatible with the decoder.
     *
     * @return a configured {@link JwtDecoder} instance
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        String secret = jwtProperties.getSecret();
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    /**
     * Creates a {@link JwtAuthenticationConverter} bean used by Spring Security
     * to convert JWT claims into granted authorities.
     * <p>
     * Customizations:
     * <ul>
     *     <li>Uses {@link JwtClaims#ROLES} as the source of authorities</li>
     *     <li>Removes the default {@code ROLE_} prefix to avoid duplication</li>
     * </ul>
     *
     * <p>
     * This ensures that roles stored in the JWT are directly mapped to
     * Spring Security authorities.
     *
     * @return a configured {@link JwtAuthenticationConverter}
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName(JwtClaims.ROLES); // add roles to JWT
        converter.setAuthorityPrefix(""); // no double ROLE_ tag

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);

        return jwtConverter;
    }
}
