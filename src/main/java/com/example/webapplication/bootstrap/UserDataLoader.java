package com.example.webapplication.bootstrap;

import com.example.webapplication.entities.Authority;
import com.example.webapplication.entities.User;
import com.example.webapplication.repositories.security.AuthorityRepository;
import com.example.webapplication.repositories.security.UserRepository;
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

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadUserData();
    }

    private void loadUserData() {
        if (authorityRepository.count() == 0) {
            Authority adminAuthority = authorityRepository.save(Authority.builder().role("ROLE_ADMIN").build());
            Authority userAuthority = authorityRepository.save(Authority.builder().role("ROLE_USER").build());
            Authority noneAuthority = authorityRepository.save(Authority.builder().role("ROLE_NONE").build());

            Set<Authority> authorities = new HashSet<>();
            authorities.add(adminAuthority);
            userRepository.save(User.builder()
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
            userRepository.save(User.builder()
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
            userRepository.save(User.builder()
                    .firstname("None")
                    .lastname("None")
                    .username("none")
                    .email("none@none.org")
                    .password(passwordEncoder.encode("none"))
                    .authorities(authorities)
                    .build()
            );

            log.debug("Users loaded: {}", userRepository.count());
        }
    }
}
