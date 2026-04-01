package com.example.webapplication.security.jwt;

import com.example.webapplication.entities.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link JwtService}.
 *
 * Verifies correct JWT generation, structure, and error handling.
 */
@ExtendWith(MockitoExtension.class)
class JwtServiceTest extends JwtBaseTest {

    @BeforeEach
    void setUp() {
        SECRET = "my-super-secret-key-my-super-secret-key"; // >= 32 chars required
        EXPIRATION = 1000 * 60 * 10; // 10 minutes

        jwtService = new JwtService(mockJwtProperties, mockUserService);
    }

    @Test
    @DisplayName("Should generate a non-empty JWT token for a valid username")
    void shouldGenerateValidToken() {
        User user = createUser("testuser", "ROLE_USER");
        when(mockJwtProperties.getSecret()).thenReturn(SECRET);
        when(mockJwtProperties.getExpiration()).thenReturn(EXPIRATION);
        when(mockUserService.findByUserName(anyString())).thenReturn(user);

        String token = jwtService.generateToken(user.getUsername());

        assertThat(token)
                .isNotNull()
                .isNotBlank();
    }

    @Test
    @DisplayName("Should include the username as subject in the JWT")
    void shouldContainCorrectUsernameAsSubject() {
        User user = createUser("john", "ROLE_USER");
        when(mockJwtProperties.getSecret()).thenReturn(SECRET);
        when(mockJwtProperties.getExpiration()).thenReturn(EXPIRATION);
        when(mockUserService.findByUserName(anyString())).thenReturn(user);

        String token = jwtService.generateToken(user.getUsername());

        Claims claims = parseJwtToken(token);

        assertThat(claims.getSubject()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("Should include the role as a custom claim in the JWT")
    void shouldContainCorrectRoleAsCustomClaim() {
        String roleUser = "ROLE_USER";
        User user = createUser("john", roleUser);
        when(mockJwtProperties.getSecret()).thenReturn(SECRET);
        when(mockJwtProperties.getExpiration()).thenReturn(EXPIRATION);
        when(mockUserService.findByUserName(anyString())).thenReturn(user);

        String token = jwtService.generateToken(user.getUsername());

        Claims claims = parseJwtToken(token);

        List<String> claimRoles = jwtService.extractRoles(claims);
        assertThat(claimRoles)
                .isNotNull()
                .isNotEmpty()
                .containsExactly(roleUser);
    }

    @Test
    @DisplayName("Should include issued-at and expiration timestamps in the JWT")
    void shouldContainIssuedAtAndExpiration() {
        User user = createUser("user", "ROLE_USER");
        when(mockJwtProperties.getSecret()).thenReturn(SECRET);
        when(mockJwtProperties.getExpiration()).thenReturn(EXPIRATION);
        when(mockUserService.findByUserName(anyString())).thenReturn(user);
        String token = jwtService.generateToken(user.getUsername());

        Claims claims = parseJwtToken(token);

        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    @DisplayName("Should set expiration time according to configuration")
    void shouldExpireAfterConfiguredTime() {
        User user = createUser("user", "ROLE_USER");
        when(mockJwtProperties.getSecret()).thenReturn(SECRET);
        when(mockJwtProperties.getExpiration()).thenReturn(EXPIRATION);
        when(mockUserService.findByUserName(anyString())).thenReturn(user);

        String token = jwtService.generateToken("user");

        Claims claims = parseJwtToken(token);

        long diff = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();

        // allow small tolerance due to execution time differences
        assertThat(diff).isBetween(EXPIRATION - 1000, EXPIRATION + 1000);
    }

    @Test
    @DisplayName("Should throw an exception when using a weak secret key")
    void shouldThrowExceptionForWeakSecret() {
        User user = createUser("user", "ROLE_USER");
        when(mockJwtProperties.getSecret()).thenReturn("short");
        when(mockJwtProperties.getExpiration()).thenReturn(EXPIRATION);
        when(mockUserService.findByUserName(anyString())).thenReturn(user);

        assertThrows(Exception.class, () -> jwtService.generateToken(user.getUsername()));
    }

    @Test
    @DisplayName("Should throw NullPointerException when username is null")
    void shouldThrowExceptionForNullUsername() {
        assertThrows(NullPointerException.class, () -> jwtService.generateToken(null));
    }
}
