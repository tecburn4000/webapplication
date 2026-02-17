package com.example.webapplication.controller;

import com.example.webapplication.WebApplication;
import com.example.webapplication.controller.viewnames.UserViews;
import com.example.webapplication.dto.UserUpdateDto;
import com.example.webapplication.dto.mapper.UserMapper;
import com.example.webapplication.entities.User;
import com.example.webapplication.service.UserProfileFacade;
import com.example.webapplication.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.ui.Model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = WebApplication.class)
class UserControllerTest extends BaseControllerIntegrationTest {

    @Autowired
    private UserProfileFacade userProfileFacade;

    @MockitoSpyBean
    private UserMapper spyUserMapper;

    @MockitoBean
    private UserService mockUserService;

    @MockitoBean
    private UserProfileFacade spyUserProfileFacade;

    @DisplayName("Show user profile")
    @Nested
    class ShowUserProfile {
        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void testShowProfileFormEditFalse() throws Exception {
            // given
            User userEntity = User.builder().username("user").build();
            UserUpdateDto userDto = spyUserMapper.toUserUpdateDTO(userEntity);

            // prepare mocks
            when(mockUserService.findByUserName("user")).thenReturn(userEntity);
            when(spyUserMapper.toUserUpdateDTO(userEntity)).thenReturn(userDto);

            mockMvc.perform(get("/users/profile"))
                    .andExpect(status().isOk())
                    .andExpect(view().name(UserViews.USERS_PROFILE))
                    .andExpect(model().attributeExists("user"))
                    .andExpect(model().attribute("editMode", false));
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void testShowProfileFormEditTrue() throws Exception {
            // given
            User userEntity = User.builder().username("user").build();
            UserUpdateDto userDto = spyUserMapper.toUserUpdateDTO(userEntity);

            // prepare mocks
            when(mockUserService.findByUserName("user")).thenReturn(userEntity);
            when(spyUserMapper.toUserUpdateDTO(userEntity)).thenReturn(userDto);

            // edit=true
            mockMvc.perform(get("/users/profile").param("edit", "true"))
                    .andExpect(status().isOk())
                    .andExpect(view().name(UserViews.USERS_PROFILE))
                    .andExpect(model().attributeExists("user"))
                    .andExpect(model().attribute("editMode", true));
        }
    }
    @DisplayName("Update user profile")
    @Nested
    class UpdateUserProfile {

        @Test
        @WithMockUser // PermissionAllUsers
        void testUpdateUserAccountEditActionWithAuth_shouldRedirect() throws Exception {
            mockMvc.perform(put("/users/profile")
                            .param("action", "edit"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/users/profile?edit=true"));
        }

        @Test
        @WithMockUser // PermissionAllUsers
        void testPostUpdateUserAccountEditActionWithAuth_sholdReturnClientError() throws Exception {
            mockMvc.perform(post("/users/profile")
                            .param("action", "edit"))
                    .andExpect(status().is4xxClientError()); // no post request implemented
        }

        @Test
        void testUpdateUserAccountEditActionWithAuth_shouldRedirect_shouldRedirectToLogin() throws Exception {
            mockMvc.perform(put("/users/profile")
                            .param("action", "edit"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER"})
        void testUpdateUserAccountSaveAction() throws Exception {
            UserUpdateDto userDto = UserUpdateDto.builder().username("user").build();

            mockMvc.perform(put("/users/profile")
                            .param("action", "save")
                            .flashAttr("editMode", "true")
                            .flashAttr("user", userDto))
                    .andExpect(status().isOk())
                    .andExpect(view().name(UserViews.USERS_PROFILE));
        }
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void updateUserAccount_shouldDelegateToFacade_andReturnView() throws Exception {

        // given
        when(userProfileFacade.handleUpdate(
                any(Model.class),
                any(UserDetails.class),
                any(UserUpdateDto.class),
                eq("save"),
                eq(UserViews.USERS_PROFILE)
        )).thenReturn((UserViews.USERS_PROFILE));

        // when + then
        mockMvc.perform(put("/users/profile")
                        .param("action", "save")
                        .flashAttr("editMode", "false") // target view ("users/profile" ==> fragments/profile-form.html) needs "editMode"
                        .flashAttr("user", UserUpdateDto.builder().username("user").build())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name((UserViews.USERS_PROFILE)));

        // verify delegation
        verify(userProfileFacade).handleUpdate(
                any(Model.class),
                any(UserDetails.class),
                any(UserUpdateDto.class),
                eq("save"),
                eq(UserViews.USERS_PROFILE)
        );
    }
}