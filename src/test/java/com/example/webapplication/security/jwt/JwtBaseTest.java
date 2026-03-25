package com.example.webapplication.security.jwt;

import com.example.webapplication.entities.Authority;
import com.example.webapplication.entities.User;
import com.example.webapplication.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.mockito.Mock;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public class JwtBaseTest {

    @Mock
    protected JwtProperties mockJwtProperties;

    @Mock
    protected UserService mockUserService;

    protected JwtService jwtService;

    protected String SECRET;
    protected long EXPIRATION;

    protected Claims parseJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    protected User createUser(String username, String role) {
        return User.builder()
                .username(username)
                .authorities(
                        Set.of(
                                Authority.builder()
                                        .role(role)
                                        .build()))
                .build();
    }
}
