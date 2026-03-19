package com.example.webapplication.config.security.jwt;

import com.example.webapplication.config.security.properties.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * Service responsible for generating JSON Web Tokens (JWT).
 * <p>
 * This class creates signed tokens based on a username and the configuration
 * provided in {@code JwtProperties}, such as the secret key and expiration time.
 */
@AllArgsConstructor
@Service
public class JwtService {

    private final JwtProperties jwtProperties;


    /**
     * Generates a signed JSON Web Token (JWT) for the given username.
     * <p>
     * The generated token contains:
     * <ul>
     *     <li>the username as the subject</li>
     *     <li>the issued-at timestamp</li>
     *     <li>an expiration timestamp based on the configured expiration time</li>
     * </ul>
     * The token is signed using the HS256 algorithm and the configured secret key.
     *
     * @param username the username to be set as the subject of the token
     * @return a signed JWT as a String
     * @throws io.jsonwebtoken.security.WeakKeyException if the provided secret key is too weak
     * @throws IllegalArgumentException if the username or secret key is invalid
     */
    public String generateToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(
                        Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }
}