package com.example.webapplication.security;


import com.example.webapplication.entities.Authority;
import com.example.webapplication.entities.User;
import com.example.webapplication.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of {@link UserDetailsService} that loads user-specific data
 * from the database using JPA.
 * <p>
 * This service acts as an adapter between the application's internal
 * {@link User} entity and Spring Security's {@link UserDetails} model.
 * It is used by Spring Security during the authentication process to
 * retrieve user credentials and authorities.
 *
 * <p><b>Responsibilities:</b>
 * <ul>
 *     <li>Load users from the database via {@link UserRepository}</li>
 *     <li>Map {@link User} entities to Spring Security {@link UserDetails}</li>
 *     <li>Convert domain-specific {@link Authority} objects into
 *         {@link GrantedAuthority} instances</li>
 * </ul>
 *
 * <p><b>Authentication flow:</b>
 * <ol>
 *     <li>Spring Security calls {@link #loadUserByUsername(String)}</li>
 *     <li>User is fetched from the database</li>
 *     <li>If not found, a {@link UsernameNotFoundException} is thrown</li>
 *     <li>User properties and authorities are mapped to {@link UserDetails}</li>
 * </ol>
 *
 * <p><b>Transaction behavior:</b>
 * The method is executed within a transactional context to ensure that
 * lazy-loaded associations (e.g. authorities) are properly initialized.
 *
 * <p><b>Security Note:</b>
 * Ensure that passwords are stored securely (e.g. BCrypt) and that
 * authority names follow the expected Spring Security conventions.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by username from the database and maps it to a
     * {@link UserDetails} instance used by Spring Security.
     *
     * @param username the username identifying the user whose data is required
     * @return a fully populated {@link UserDetails} instance
     * @throws UsernameNotFoundException if the user could not be found
     */
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("Getting User info via JPA");

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User name: " + username + " not found!"));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(),
                user.isAccountNonLocked(), convertToSpringAuthorities(user.getAuthorities()));
    }


    /**
     * Converts a set of domain-specific {@link Authority} entities into
     * Spring Security {@link GrantedAuthority} instances.
     * <p>
     * Each authority is mapped to a {@link SimpleGrantedAuthority}
     * using its role name.
     *
     * @param authorities the set of domain authorities (may be {@code null} or empty)
     * @return a collection of {@link GrantedAuthority}, never {@code null}
     */
    private Collection<? extends GrantedAuthority> convertToSpringAuthorities(Set<Authority> authorities) {
        if (authorities != null && !authorities.isEmpty()){
            return authorities.stream()
                    .map(Authority::getRole)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }
}

