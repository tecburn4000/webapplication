package com.example.webapplication.controller;

import com.example.webapplication.WebApplication;
import com.example.webapplication.controller.viewnames.AdminUserViews;
import com.example.webapplication.dto.UserUpdateDto;
import com.example.webapplication.dto.mapper.UserMapper;
import com.example.webapplication.entities.User;
import com.example.webapplication.service.UserProfileFacade;
import com.example.webapplication.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = WebApplication.class)
class AdminUserControllerTest extends BaseControllerIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileFacade userProfileFacade;

    @MockitoSpyBean
    private UserMapper spyUserMapper;

    @MockitoBean
    private UserProfileFacade spyUserProfileFacade;

    @MockitoBean
    private UserService mockUserService;

    @DisplayName("List users")
    @Nested
    class ListUsers{

        @Test
        @WithMockUser(roles = "ADMIN")
        void listUsers_shouldReturnAdminListView() throws Exception {

            when(mockUserService.findAllUsers()).thenReturn(List.of());

            mockMvc.perform(get("/admin"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/list"))
                    .andExpect(model().attributeExists("users"));

            verify(mockUserService).findAllUsers();
        }

        @Test
        @WithMockUser(roles = "USER")
        void listUsers_withNonAdmin_shouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/admin"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void listUsers_withoutAuth_shouldRedirect() throws Exception {
            mockMvc.perform(get("/admin"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @DisplayName("Update users")
    @Nested
    class UpdateUsers {

       // GET /admin/update/{id}

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateUser_shouldPopulateModel_andReturnUpdateView() throws Exception {

            Long id = 1L;
            User user = new User();
            UserUpdateDto dto = new UserUpdateDto();

            when(userService.findById(id)).thenReturn(user);
            when(spyUserMapper.toUserUpdateDTO(user)).thenReturn(dto);

            mockMvc.perform(get("/admin/update/{id}", id)
                            .param("edit", "true"))
                    .andExpect(status().isOk())
                    .andExpect(view().name(AdminUserViews.ADMIN_UPDATE))
                    .andExpect(model().attributeExists("user"))
                    .andExpect(model().attribute("editMode", true));

            verify(userService).findById(id);
            verify(spyUserMapper).toUserUpdateDTO(user);
        }

        @Test
        void updateUserWithoutAuth_shouldRedirectToLogin() throws Exception {

            mockMvc.perform(get("/admin/update/{id}", 1L)
                            .param("edit", "true"))
                    .andExpect(status().is3xxRedirection());
        }

        // POST /admin/update/{id}?action=edit
        @Test
        void showEditProfile_missingEditParameter_shouldRedirectLogin() throws Exception {
            mockMvc.perform(post("/admin/update/{id}", 1L))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void showEditProfile_shouldRedirectToEditMode() throws Exception {
            Long id = 1L;
            mockMvc.perform(put("/admin/update/{id}", id)
                            .param("action", "edit"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/update/" + id + "?edit=true"));
        }

        // POST /admin/update/{id}?action=save

        @Test
        @WithMockUser(username = "admin", roles = "ADMIN")
        void updateUserAccount_shouldDelegateToFacade() throws Exception {

            Long id = 1L;

            when(userProfileFacade.handleUpdate(
                    any(Model.class),
                    any(UserDetails.class),
                    any(UserUpdateDto.class),
                    eq("save"),
                    eq(AdminUserViews.REDIRECT_ADMIN)
            )).thenReturn(AdminUserViews.REDIRECT_ADMIN);

            mockMvc.perform(put("/admin/update/{id}", id)
                            .param("action", "save")
                            .flashAttr("user", UserUpdateDto.builder().username("user").build()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(AdminUserViews.ADMIN));

            verify(userProfileFacade).handleUpdate(
                    any(Model.class),
                    any(UserDetails.class),
                    any(UserUpdateDto.class),
                    eq("save"),
                    eq(AdminUserViews.REDIRECT_ADMIN)
            );
        }

        @Test
        void updateUserAccount_withoutAuthentication_shouldRedirectToLogin() throws Exception {

            mockMvc.perform(post("/admin/update/{id}", 1L)
                            .param("action", "save"))
                    .andExpect(status().is3xxRedirection());
        }
    }

//    TODO
//    - save users
//    - delete users
//    - register

}