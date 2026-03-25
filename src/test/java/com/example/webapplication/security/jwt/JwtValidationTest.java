package com.example.webapplication.config.security.jwt;

import com.example.webapplication.entities.User;
import com.example.webapplication.security.jwt.JwtBaseTest;
import com.example.webapplication.security.jwt.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Tests for validating and parsing JWT tokens.
 *
 * Focuses on signature verification, expiration handling,
 * and detection of tampered tokens.
 */
@ExtendWith(MockitoExtension.class)
class JwtValidationTest extends JwtBaseTest {

    @BeforeEach
    void setUp() {
        SECRET = "my-super-secret-key-my-super-secret-key"; // >= 32 chars required
        EXPIRATION = 1000 * 2; // 2 seconds for testing

        jwtService = new JwtService(mockJwtProperties, mockUserService);
    }

    @Test
    @DisplayName("Should successfully parse a valid JWT token")
    void shouldParseValidToken() {
        User user = createUser("user", "ROLE_USER");
        when(mockJwtProperties.getSecret()).thenReturn(SECRET);
        when(mockJwtProperties.getExpiration()).thenReturn(EXPIRATION);
        when(mockUserService.findByUserName(anyString())).thenReturn(user);

        String token = jwtService.generateToken(user.getUsername());

        Claims claims = parseJwtToken(token);
        assertThat(claims.getSubject()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("Should throw exception when token signature is invalid")
    void shouldFailWhenSignatureIsInvalid() {
        User user = createUser("user", "ROLE_USER");
        when(mockJwtProperties.getSecret()).thenReturn(SECRET);
        when(mockJwtProperties.getExpiration()).thenReturn(EXPIRATION);
        when(mockUserService.findByUserName(anyString())).thenReturn(user);

        String token = jwtService.generateToken(user.getUsername());

        // manipulate token (simple but effective)
        String manipulatedToken = token + "tampered";

        assertThrows(SecurityException.class, () -> parseJwtToken(manipulatedToken));
    }

    @Test
    @DisplayName("Should throw exception when token is expired")
    void shouldFailWhenTokenIsExpired() throws InterruptedException {
        User user = createUser("user", "ROLE_USER");
        when(mockJwtProperties.getSecret()).thenReturn(SECRET);
        when(mockJwtProperties.getExpiration()).thenReturn(EXPIRATION);
        when(mockUserService.findByUserName(anyString())).thenReturn(user);

        String token = jwtService.generateToken(user.getUsername());

        // wait until token expires
        Thread.sleep(EXPIRATION + 500);

        assertThrows(ExpiredJwtException.class, () -> parseJwtToken(token));
    }

    @Test
    @DisplayName("Should throw exception when using wrong secret key")
    void shouldFailWithWrongSecret() {
        User user = createUser("user", "ROLE_USER");
        when(mockJwtProperties.getSecret()).thenReturn(SECRET);
        when(mockJwtProperties.getExpiration()).thenReturn(EXPIRATION);
        when(mockUserService.findByUserName(anyString())).thenReturn(user);

        String token = jwtService.generateToken(user.getUsername());

        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor("wrong-secret-key-wrong-secret-key".getBytes(StandardCharsets.UTF_8)))
                .build();

        assertThrows(SecurityException.class, () ->jwtParser.parseClaimsJws(token));
    }

    @Test
    @DisplayName("Should detect malformed JWT token")
    void shouldFailForMalformedToken() {
        String invalidToken = "this.is.not.a.jwt";

        assertThrows(MalformedJwtException.class, () -> parseJwtToken(invalidToken));
    }
}