package com.example.webapplication.controller;

import com.example.webapplication.controller.viewnames.UserViews;
import com.example.webapplication.dto.UpdatePasswordDto;
import com.example.webapplication.dto.UserUpdateDto;
import com.example.webapplication.dto.mapper.UserMapper;
import com.example.webapplication.entities.User;
import com.example.webapplication.security.permissions.PermissionAllUsers;
import com.example.webapplication.service.UserProfileFacade;
import com.example.webapplication.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("/users")
public class UserController {

    public static final String EDIT_MODE = "editMode";
    private final UserService userService;
    private final UserProfileFacade userProfileFacade;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @PermissionAllUsers
    @GetMapping("/profile")
    public String showProfileForm(
            @RequestParam(defaultValue = "false") boolean edit,
            @AuthenticationPrincipal UserDetails user,
            Model model) {
        UserUpdateDto userUpdateDto = userMapper.toUserUpdateDTO(userService.findByUserName(user.getUsername()));
        model.addAttribute("user", userUpdateDto);
        model.addAttribute(EDIT_MODE, edit);
        return UserViews.USERS_PROFILE;
    }

    @PermissionAllUsers
    @PostMapping(value = "/profile", params = "action=edit")
    public String showEditProfile() {
        return "redirect:/users/profile?edit=true";
    }

    @PermissionAllUsers
    @PostMapping(value = "/profile", params = "action=save")
    public String updateUserAccount(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute("user") UserUpdateDto userUpdateDto,
            @RequestParam String action) {

        if (!action.equals("save")) {
            throw new IllegalArgumentException("Unknown action: " + action);
        }

        return userProfileFacade.handleUpdate(
                model,
                userDetails,
                userUpdateDto,
                action,
                UserViews.USERS_PROFILE
        );
    }

    public void updatePassword(UpdatePasswordDto updatePasswordDto, String username) {
        // TODO: to be done
        User user = userService.findByUserName(username);

        validatePasswords(updatePasswordDto);

        if (!passwordEncoder.matches(updatePasswordDto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password wrong");
        }

        user.setPassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));
        userService.save(user);
    }

    private static void validatePasswords(UpdatePasswordDto updatePasswordDto) {
        // check if password entries are valid
        if (updatePasswordDto.getNewPassword() == null ||
                updatePasswordDto.getNewPassword().isEmpty() ||
                updatePasswordDto.getOldPassword() == null ||
                updatePasswordDto.getOldPassword().isEmpty()) {
            // TODO: create specific exception
            throw new RuntimeException("Old Password or New Password is null or empty!");
        }
    }

//    private static void logErrors(Errors errors) {
//        if (log.isWarnEnabled()) {
//            errors. getAllErrors().forEach(error ->
//                    log.warn(Objects.requireNonNull(error).getDefaultMessage())
//            );
//        }
//    }
}
