package com.example.webapplication.security.jwt;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for JWT (JSON Web Token) settings.
 * <p>
 * This class is used to bind external configuration properties (e.g. from
 * {@code application.yml} or {@code application.properties}) with the prefix
 * {@code app.jwt} to strongly typed fields.
 *
 * <p><b>Example configuration:</b>
 * <pre>
 * app.jwt.secret=your-secret-key
 * app.jwt.expiration=600000
 * </pre>
 *
 * <p><b>Properties:</b>
 * <ul>
 *     <li><b>secret</b> - Secret key used for signing and validating JWT tokens</li>
 *     <li><b>expiration</b> - Token validity duration in milliseconds</li>
 * </ul>
 *
 * <p><b>Default values:</b>
 * <ul>
 *     <li>secret = "secret"</li>
 *     <li>expiration = 600000 (10 minutes)</li>
 * </ul>
 *
 * <p><b>Security Note:</b>
 * The default secret should never be used in production. Always configure
 * a strong, sufficiently long, and random secret key via external configuration.
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /**
     * Secret key used to sign and validate JWT tokens.
     * <p>
     * Must be a sufficiently long and secure value to ensure proper
     * cryptographic strength for HMAC-based algorithms (e.g. HS256).
     */
    private String secret = "secret";

    /**
     * Expiration time of the JWT token in milliseconds.
     * <p>
     * Defines how long a token remains valid after its creation.
     */
    private long expiration = 600000;
}
