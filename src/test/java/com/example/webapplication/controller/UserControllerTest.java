package com.example.webapplication.controller;

import com.example.webapplication.entities.User;
import com.example.webapplication.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@SpringBootTest
class UserControllerTest extends BaseControllerIntegrationTest {

    @DisplayName("Read Users")
    @Nested
    class ReadUsers{

        /**
         * Test with User
         */
        @Test
        @WithUserDetails("user")
        void readUsers() throws Exception {

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("users/list"));
        }

        @Test
        void readUsersNotAuth() throws Exception {
            mockMvc.perform(get("/users"))
                    .andExpect(status().is3xxRedirection());
        }

    }
}