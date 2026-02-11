package com.example.webapplication.validation;

import com.example.webapplication.dto.DtoPasswordMatcher;
import com.example.webapplication.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates that passwords are equal and follow a given pattern
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, DtoPasswordMatcher> {

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
    public boolean isValid(@NonNull DtoPasswordMatcher dtoPasswordMatcher, ConstraintValidatorContext context){
        String pwd = dtoPasswordMatcher.getPassword();
        String matchingPwd = dtoPasswordMatcher.getMatchingPassword();

        if (pwd == null || pwd.isBlank()) {
            return true;
        }

        if (pwd.equals(matchingPwd)) {
            Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
            Matcher matcher = pattern.matcher(dtoPasswordMatcher.getPassword());
            return matcher.matches();
        }
        return false;
    }
}