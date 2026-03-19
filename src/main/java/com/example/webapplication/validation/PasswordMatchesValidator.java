package com.example.webapplication.validation;

import com.example.webapplication.dto.user.PasswordMatcherDto;
import com.example.webapplication.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.NonNull;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates that passwords are equal and follow a given pattern
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, PasswordMatcherDto> {

    // ^: indicates the string's beginning
    // (?=.*[a-z]): makes sure that there is at least one small letter
    // (?=.*[A-Z]): needs at least one capital letter
    // (?=.*\\d): requires at least one digit
    // (?=.*[@#$%^&+=]): provides a guarantee of at least one special symbol
    // .{8,50}: imposes the minimum length of 8 characters and the maximum length of 20 characters
    // $: terminates the string
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,50}$";

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        // nothing to initialize for password matching
    }

    @Override
    public boolean isValid(@NonNull PasswordMatcherDto passwordMatcherDto, ConstraintValidatorContext context){
        String pwd = Objects.requireNonNull(passwordMatcherDto.getPassword(), "Password is required!");
        String matchingPwd = Objects.requireNonNull(passwordMatcherDto.getMatchingPassword(), "Matching password is required!");

        // TODO: mover password from user dto to password dto!
//        if (pwd == null || pwd.isBlank()) {
//            return true;
//        }

        if (pwd.equals(matchingPwd)) {
            Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
            Matcher matcher = pattern.matcher(passwordMatcherDto.getPassword());
            return matcher.matches();
        }
        return false;
    }
}