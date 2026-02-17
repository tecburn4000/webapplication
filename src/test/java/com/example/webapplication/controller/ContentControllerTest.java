package com.example.webapplication.controller;

import com.example.webapplication.bootstrap.UserDataLoader;
import com.example.webapplication.entities.User;
import com.example.webapplication.repositories.security.UserRepository;
import com.example.webapplication.security.JpaUserDetailsService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class ContentControllerTest extends BaseControllerIntegrationTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JpaUserDetailsService jpaUserDetailsService;

    @DisplayName("Init Login Form")
    @Nested
    class LoginForm{

        @Test
        @WithUserDetails
        void initLoginFormAuth() throws Exception {

            mockMvc.perform(get("/login"))//.with(httpBasic("user", "password")))
                    .andExpect(status().isOk())
                    .andExpect(view().name("login"));
        }

        @Test
        void initLoginFormNotAuth() throws Exception {
            mockMvc.perform(get("/login"))
                    .andExpect(status().isOk());
        }

    }


    @DisplayName("Index Form Tests")
    @Nested
    class IndexFormTests {

        /**
         * Test with User
         */
        @Test
        @WithUserDetails("user")
        void initIndexFormWithAuth() throws Exception {
            mockMvc.perform(get("/index"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));
        }

        /**
         * Test with all users set up in {@link UserDataLoader}
         *
         * @throws Exception
         */
        @Test
        @Disabled
        void initIndexFormWithRealUsers() throws Exception {

            List<User> users = userRepository.findAll();
            for(User user : users){
                UserDetails userDetails = jpaUserDetailsService.loadUserByUsername(user.getUsername());
                mockMvc.perform(get("/index").with(user(userDetails)))
                        .andExpect(status().isOk())
                        .andExpect(view().name("index"));
            }
        }

        /**
         * Ensure that all "unauthorized" attempts to open the "index.html" are redirected to login-page
         *
         * @throws Exception
         */
        @Test
        void initIndexFormWithNoUserRedirect() throws Exception {
            mockMvc.perform(get("/index"))
                    .andExpect(status().is3xxRedirection());
        }
    }
}