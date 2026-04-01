package com.example.webapplication.security.jwt;

import com.example.webapplication.entities.Authority;
import com.example.webapplication.entities.User;
import com.example.webapplication.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Service responsible for creating and inspecting JSON Web Tokens (JWT).
 * <p>
 * This service generates signed JWTs for authenticated users and provides
 * helper methods to extract information such as roles from token claims.
 * <p>
 * The token is signed using the secret defined in {@link JwtProperties}
 * and contains standard claims such as subject, issue date, expiration date,
 * as well as custom claims like user roles.
 *
 * <p><b>Responsibilities:</b>
 * <ul>
 *     <li>Generate JWT tokens for authenticated users</li>
 *     <li>Embed user roles into token claims</li>
 *     <li>Extract roles from JWT claims</li>
 *     <li>Provide debugging support for inspecting token contents</li>
 * </ul>
 *
 * <p><b>Security Note:</b>
 * The secret key must be kept secure. Exposure of the secret allows
 * attackers to forge valid tokens.
 */
@Slf4j
@AllArgsConstructor
@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final UserService userService;

    /**
     * Generates a signed JWT token for the given username.
     * <p>
     * The generated token includes:
     * <ul>
     *     <li>Subject (username)</li>
     *     <li>User roles as a custom claim</li>
     *     <li>Issued timestamp</li>
     *     <li>Expiration timestamp</li>
     * </ul>
     *
     * @param username the username for which the token should be generated (must not be {@code null})
     * @return a signed JWT token as a compact string
     * @throws NullPointerException if {@code username} is {@code null}
     */
    public String generateToken(String username) {

        Objects.requireNonNull(username, "Username must not be null");

        User user = userService.findByUserName(username);
        List<String> roles = user.getAuthorities().stream()
                .map(Authority::getRole)
                .toList();

        String jwtString = Jwts.builder()
                .setSubject(username)
                .claim(JwtClaims.ROLES, roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(
                        Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()),
                        SignatureAlgorithm.HS256
                )
                .compact();

        if (log.isDebugEnabled()) {
            debugJwtToken(jwtString);
        }

        return jwtString;
    }

    /**
     * Extracts the list of roles from the given JWT claims.
     * <p>
     * If the roles claim is missing or not in the expected format,
     * an empty list is returned.
     *
     * @param claims the JWT claims containing the roles information
     * @return a list of roles as strings, or an empty list if not present
     */
    public List<String> extractRoles(Claims claims) {
        Object rolesClaim = claims.get(JwtClaims.ROLES);
        if (!(rolesClaim instanceof List<?> rolesList)) {
            return List.of();
        }

        return rolesList.stream()
                .map(Object::toString)
                .toList();
    }

    /**
     * Logs the contents of a JWT token for debugging purposes.
     * <p>
     * The token is parsed and all contained claims are printed
     * to the debug log output.
     *
     * <p><b>Important:</b>
     * This method should only be used in development or debugging scenarios,
     * as it may expose sensitive information.
     *
     * @param token the JWT token to inspect
     */
    public void debugJwtToken(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecret().getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            builder.append("\t").append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
        }
        log.debug("\nJWT Claims:\n{}", builder);
    }
}
