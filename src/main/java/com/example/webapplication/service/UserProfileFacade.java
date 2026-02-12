package com.example.webapplication.service;

import com.example.webapplication.dto.UserUpdateDto;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;


@AllArgsConstructor
@Service
public class UserProfileFacade {

    private final UserService userService;

    public String handleUpdate(
            Model model,
            UserDetails userDetails,
            UserUpdateDto userUpdateDto,
            String action,
            String successView) {

        if (!action.equals("save")) {
            throw new IllegalArgumentException("Unknown action: " + action);
        }

        if (userDetails != null) {
            userService.updateExistingUser(userUpdateDto);
        }

        model.addAttribute("editMode", false);
        return successView;
    }
}