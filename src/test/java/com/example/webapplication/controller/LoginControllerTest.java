package com.example.webapplication.controller;

import com.example.webapplication.security.JpaUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
class LoginControllerTest extends BaseControllerIntegrationTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JpaUserDetailsService jpaUserDetailsService;

    @DisplayName("Init Login Form")
    @Nested
    class LoginForm{

        @Test
        @WithUserDetails
        void initLoginFormAuth() throws Exception {

            mockMvc.perform(get("/login"))
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
