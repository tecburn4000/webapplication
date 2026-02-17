package com.example.webapplication.controller;

import com.example.webapplication.dto.UserRegistrationDto;
import com.example.webapplication.service.UserService;
import com.example.webapplication.service.exception.UserAlreadyExistException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String showRegistrationForm(Model model) {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        model.addAttribute("user", userRegistrationDto);
        return "/register";
    }

    @PostMapping
    public String registerUserAccount(
            @Valid @ModelAttribute("user") UserRegistrationDto userRegistrationDto,
            BindingResult bindingResult,
            Errors errors) {

        if (errors.hasErrors()) {
            throw new RuntimeException("TODO: Handle Errors!");

            // In case of validation errors redirect to actual view.
            //return "redirect:/login";
        }

        try {
            userRegistrationDto.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
            userService.registerNewUser(userRegistrationDto);
        } catch (UserAlreadyExistException uaeEx) {
            return "registration_error";
        }

        return "login";
    }

//    TODO
//    - register tests

}
