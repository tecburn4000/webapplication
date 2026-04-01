package com.example.webapplication.bootstrap;

import com.example.webapplication.entities.Authority;
import com.example.webapplication.entities.User;
import com.example.webapplication.service.AuthorityService;
import com.example.webapplication.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Component
public class UserDataLoader implements CommandLineRunner {

    private final UserService userService;
    private final AuthorityService authorityService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadUserData();
    }

    private void loadUserData() {
        if (authorityService.count() == 0) {
            Authority adminAuthority = authorityService.save(Authority.builder().role("ROLE_ADMIN").build());
            Authority userAuthority = authorityService.save(Authority.builder().role("ROLE_USER").build());
            Authority noneAuthority = authorityService.save(Authority.builder().role("ROLE_NONE").build());

            Set<Authority> authorities = new HashSet<>();
            authorities.add(adminAuthority);
            userService.save(User.builder()
                    .firstname("Admin")
                    .lastname("Admin")
                    .username("admin")
                    .email("admin@admin.org")
                    .password(passwordEncoder.encode("admin"))
                    .authorities(authorities)
                    .build()
            );

            authorities.clear();
            authorities.add(userAuthority);
            userService.save(User.builder()
                    .firstname("User")
                    .lastname("User")
                    .username("user")
                    .email("user@user.org")
                    .password(passwordEncoder.encode("password"))
                    .authorities(authorities)
                    .build()
            );

            authorities.clear();
            authorities.add(noneAuthority);
            userService.save(User.builder()
                    .firstname("None")
                    .lastname("None")
                    .username("none")
                    .email("none@none.org")
                    .password(passwordEncoder.encode("none"))
                    .authorities(authorities)
                    .build()
            );

            log.debug("Users loaded: {}", userService.count());
        }
    }
}

