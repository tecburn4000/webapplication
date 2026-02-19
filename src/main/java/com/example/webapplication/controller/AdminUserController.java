package com.example.webapplication.controller;

import com.example.webapplication.controller.viewnames.AdminUserViews;
import com.example.webapplication.dto.UpdatePasswordDto;
import com.example.webapplication.dto.UserUpdateDto;
import com.example.webapplication.dto.mapper.UserMapper;
import com.example.webapplication.security.permissions.PermissionAdmin;
import com.example.webapplication.service.UserProfileFacade;
import com.example.webapplication.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminUserController {

    public static final String EDIT_MODE = "editMode";
    private final UserService userService;
    private final UserProfileFacade userProfileFacade;
    private final UserMapper userMapper;

    @PermissionAdmin
    @GetMapping
    public String listUsers(Model model){
        model.addAttribute("users", userService.findAllUsers());
        return "admin/list";
    }

    @PermissionAdmin
    @GetMapping("/update/{id}")
    public String updateUser(
            @RequestParam(defaultValue = "false") boolean edit,
            @PathVariable Long id,
            Model model) {
        UserUpdateDto userUpdateDto = userMapper.toUserUpdateDTO(userService.findById(id));
        model.addAttribute("user", userUpdateDto);
        model.addAttribute(EDIT_MODE, edit);
        return AdminUserViews.ADMIN_UPDATE;
    }

    @PermissionAdmin
    @PutMapping(value = "/update/{id}", params = "action=edit")
    public String showEditProfile(@PathVariable Long id) {
        return "redirect:/admin/update/" + id + "?edit=true";
    }

    @PermissionAdmin
    @PutMapping(value = "/update/{id}", params = "action=save")
    public String updateUserAccount(
            Model model,
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute("user") UserUpdateDto userUpdateDto,
            @RequestParam String action) {

        return userProfileFacade.handleUpdate(
                model,
                userDetails,
                userUpdateDto,
                action,
                AdminUserViews.REDIRECT_ADMIN
        );
    }

    @PermissionAdmin
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return AdminUserViews.REDIRECT_ADMIN;
    }
}
