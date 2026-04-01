package com.example.webapplication.security;

import com.example.webapplication.entities.Authority;
import com.example.webapplication.entities.User;
import com.example.webapplication.repositories.security.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link JpaUserDetailsService}.
 * <p>
 * This test class verifies the behavior of the service in isolation by mocking
 * the {@link UserRepository}.
 *
 * <p><b>Test scope:</b>
 * <ul>
 *     <li>Successful user loading</li>
 *     <li>Exception handling when user is not found</li>
 *     <li>Correct mapping of authorities</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JpaUserDetailsService - Unit Tests")
class JpaUserDetailsServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    JpaUserDetailsService service;

    /**
     * Verifies that a valid user is correctly loaded and mapped to {@link org.springframework.security.core.userdetails.UserDetails}.
     */
    @Test
    @DisplayName("Should load user successfully and map authorities")
    void shouldLoadUserSuccessfully() {
        // given
        User user = User.builder()
                .username("john")
                .password("password")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        Authority authority = new Authority();
        authority.setRole("ROLE_USER");

        user.setAuthorities(Set.of(authority));

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        // when
        UserDetails result = service.loadUserByUsername("john");

        // then
        assertThat(result.getUsername()).isEqualTo("john");
        assertThat(result.getPassword()).isEqualTo("password");
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    /**
     * Verifies that a {@link UsernameNotFoundException} is thrown
     * when the user cannot be found in the database.
     */
    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void shouldThrowExceptionWhenUserNotFound() {
        // given
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> service.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("unknown");
    }
}
