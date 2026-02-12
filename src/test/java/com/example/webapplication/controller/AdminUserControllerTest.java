package com.example.webapplication.controller;

import com.example.webapplication.WebApplication;
import com.example.webapplication.entities.User;
import com.example.webapplication.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(classes = WebApplication.class)
class AdminUserControllerTest extends BaseControllerIntegrationTest {

    @Autowired
    UserService userService;

    private List<User> allUsers;

    @BeforeEach
    void setUp(){
        allUsers = userService.findAllUsers();
        assertFalse(allUsers.isEmpty(), "Users should not be empty!");
    }

    @DisplayName("Read Users")
    @Nested
    class ReadUsers{

        @Test
        @WithUserDetails()
        void readUsersWithUserAuth() throws Exception {

            mockMvc.perform(get("/admin"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithUserDetails("none")
        void readUsersWithNoneAuth() throws Exception {

            mockMvc.perform(get("/admin"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithUserDetails("admin")
        void readUsersWithAdminAuth() throws Exception {

            mockMvc.perform(get("/admin"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/list"));
        }

        @Test
        void readUsersNotAuth() throws Exception {
            mockMvc.perform(get("/admin"))
                    .andExpect(status().is3xxRedirection());
        }

    }

    @DisplayName("Update Users")
    @Nested
    class UpdateUsers{

        /**
         * Test with user
         */
        @Test
        @WithUserDetails()
        void updateUsersWithUserAuth() throws Exception {
            mockMvc.perform(get("/admin/update/{id}", allUsers.getFirst().getId()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithUserDetails("none")
        void updateUsersWithNoneAuth() throws Exception {
            mockMvc.perform(get("/admin/update/{id}", allUsers.getFirst().getId()))
                    .andExpect(status().isForbidden());
        }

        /**
         * Test with admin
         */
        @Test
        @WithUserDetails("admin")
        void updateUsersWithAdmin() throws Exception {
            mockMvc.perform(get("/users/update/{id}", allUsers.getFirst().getId()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/profile"));
        }

        @Test
        void updateUsersWithNoAuthRedirect() throws Exception {
            mockMvc.perform(get("/admin/update/{id}", 1))
                    .andExpect(status().is3xxRedirection());
        }

    }

//    TODO
//    - save users
//    - delete users
//    - register

}