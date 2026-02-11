package com.example.webapplication.validation;

import com.example.webapplication.dto.UserRegistrationDto;
import com.example.webapplication.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PasswordMatchesValidatorTest {

    private PasswordMatchesValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private PasswordMatches passwordMatches;

    @BeforeEach
    void setUp() {
        validator = new PasswordMatchesValidator();
        validator.initialize(passwordMatches);
    }

    @DisplayName("Should return true when password is matching")
    @Nested
    class PasswordMatchesMatchesTests {

        @Test
        @DisplayName("Should return true when passwords match and meet pattern requirements")
        void testValidPasswordsMatch() {
            // Given
            UserRegistrationDto user = new UserRegistrationDto();
            String password = "ValidPass123@";
            user.setPassword(password);
            user.setMatchingPassword(password);

            // When
            boolean result = validator.isValid(user, context);

            // Then
            assertTrue(result, "Valid matching passwords should pass validation");
        }
    }

    @DisplayName("Password And Pattern Tests")
    @Nested
    class PasswordAndPatternTests {

        private static Stream<Arguments> passwordMatchesFailingParameters() {
            return Stream.of(
                    // Different passwords should fail
                    Arguments.of("ValidPass123@", "DifferentPass123@", "Non-matching passwords should fail validation"),
                    // To short passwords should fail
                    Arguments.of("Pass1@", "Pass1@", "Matching to short passwords should fail validation"),
                    // empty passwords should fail
                    Arguments.of("", "", "Empty passwords should fail validation")
            );
        }

        @ParameterizedTest
        @MethodSource("passwordMatchesFailingParameters")
        @DisplayName("Should return false when passwords or pattern don't match")
        void testValidPasswordsWithDifferentSpecialChars(String password, String matchingPassword, String description) {
            // Given
            UserRegistrationDto user = new UserRegistrationDto();
            user.setPassword(password);
            user.setMatchingPassword(matchingPassword);

            // When
            boolean result = validator.isValid(user, context);

            // Then
            assertFalse(result, description);
        }
    }

    @DisplayName("Missing Character Or Digit Tests")
    @Nested
    class MissingCharacterOrDigitTypeTests {
        private static Stream<Arguments> missingCharacterParameters() {
            return Stream.of(
                    // no lowercase letters
                    Arguments.of("VALIDPASS123@", "VALIDPASS123@", "Password without lowercase letter should fail validation"),
                    // no uppercase letters
                    Arguments.of("validpass123@", "validpass123@", "Password without uppercase letter should fail validation"),
                    // no digits
                    Arguments.of("ValidPassword@", "ValidPassword@", "Password without digit should fail validation"),
                    // no special characters
                    Arguments.of("ValidPass123", "ValidPass123", "Password without special character should fail validation")
            );
        }

        @ParameterizedTest
        @MethodSource("missingCharacterParameters")
        @DisplayName("Should return false when a digit or a letter is missing")
        void testCaseSensitivity(String password, String matchingPassword, String description) {
            // Given
            UserRegistrationDto user = new UserRegistrationDto();
            user.setPassword(password);
            user.setMatchingPassword(matchingPassword);

            // When
            boolean result = validator.isValid(user, context);

            // Then
            assertFalse(result, description);

        }
    }

    @DisplayName("Length Validation Tests")
    @Nested
    class LengthValidationTests {
        @Test
        @DisplayName("Should return false when password is too short (less than 8 characters)")
        void testPasswordTooShort() {
            // Given - only 7 characters
            UserRegistrationDto user = new UserRegistrationDto();
            String password = "Pass1@a";
            user.setPassword(password);
            user.setMatchingPassword(password);

            // When
            boolean result = validator.isValid(user, context);

            // Then
            assertFalse(result, "Password shorter than 8 characters should fail validation");
        }

        @Test
        @DisplayName("Should return false when password is too long (more than 50 characters)")
        void testPasswordTooLong() {
            // Given - 51 characters
            UserRegistrationDto user = new UserRegistrationDto();
            user.setPassword("ValidPass123@ValidPass123@ValidPass123@ValidPass123@A");
            user.setMatchingPassword("ValidPass123@ValidPass123@ValidPass123@ValidPass123@A");

            // When
            boolean result = validator.isValid(user, context);

            // Then
            assertFalse(result, "Password longer than 50 characters should fail validation");
        }

        @Test
        @DisplayName("Should return true when password is exactly 8 characters and valid")
        void testPasswordExactly8Characters() {
            // Given - exactly 8 characters with all requirements
            UserRegistrationDto user = new UserRegistrationDto();
            String password = "Pass123@";
            user.setPassword(password);
            user.setMatchingPassword(password);

            // When
            boolean result = validator.isValid(user, context);

            // Then
            assertTrue(result, "Valid password with exactly 8 characters should pass validation");
        }

        @Test
        @DisplayName("Should return true when password is exactly 50 characters and valid")
        void testPasswordExactly50Characters() {
            // Given - exactly 50 characters with all requirements
            UserRegistrationDto user = new UserRegistrationDto();
            String password = "ValidPass123@ValidPass123@ValidPass123@ValidPass12";
            user.setPassword(password);
            user.setMatchingPassword(password);

            // When
            boolean result = validator.isValid(user, context);

            // Then
            assertTrue(result, "Valid password with exactly 50 characters should pass validation");
        }

    }

    @DisplayName("Valid Password Tests with Different Special Characters")
    @Nested
    class ValidPasswordTestsWithDifferentSpecialCharacters {
        @ParameterizedTest
        @ValueSource(strings = {"ValidPass123@", "ValidPass123#", "ValidPass123$",
                "ValidPass123%", "ValidPass123^", "ValidPass123&",
                "ValidPass123+", "ValidPass123="})
        @DisplayName("Should return true for valid passwords with different allowed special characters")
        void testValidPasswordsWithDifferentSpecialChars(String password) {
            // Given
            UserRegistrationDto user = new UserRegistrationDto();
            user.setPassword(password);
            user.setMatchingPassword(password);

            // When
            boolean result = validator.isValid(user, context);

            // Then
            assertTrue(result, "Valid password with special character '" +
                    password.charAt(password.length() - 1) + "' should pass validation");
        }

        @Test
        @DisplayName("Should return false when password has invalid special character")
        void testInvalidSpecialCharacter() {
            // Given - using '!' which is not in the allowed set
            UserRegistrationDto user = new UserRegistrationDto();
            user.setPassword("ValidPass123!");
            user.setMatchingPassword("ValidPass123!");

            // When
            boolean result = validator.isValid(user, context);

            // Then
            assertFalse(result, "Password with invalid special character should fail validation");
        }

    }

    @DisplayName("Null Handling Tests")
    @Nested
    class NullHandlingTests {
        @Test
        @DisplayName("Should throw NullPointerException when UserDto is null")
        void testNullUserDto() {
            // Given
            UserRegistrationDto user = null;

            // When/Then
            assertThrows(NullPointerException.class, () -> {
                validator.isValid(user, context);
            }, "Null UserDto should throw NullPointerException");
        }

        @Test
        @DisplayName("Should throw NullPointerException when password is null")
        void testNullPassword() {
            // Given
            UserRegistrationDto user = new UserRegistrationDto();
            user.setPassword(null);
            user.setMatchingPassword("ValidPass123@");

            // When/Then
            assertThrows(NullPointerException.class, () -> {
                validator.isValid(user, context);
            }, "Null password should throw NullPointerException");
        }

        @Test
        @DisplayName("Should throw NullPointerException when matching password is null")
        void testNullMatchingPassword() {
            // Given
            UserRegistrationDto user = new UserRegistrationDto();
            user.setPassword("ValidPass123@");
            user.setMatchingPassword(null);

            // When/Then
            assertThrows(NullPointerException.class, () -> {
                validator.isValid(user, context);
            }, "Null matching password should throw NullPointerException");
        }

        @Test
        @DisplayName("Should throw NullPointerException when both passwords are null")
        void testBothPasswordsNull() {
            // Given
            UserRegistrationDto user = new UserRegistrationDto();
            user.setPassword(null);
            user.setMatchingPassword(null);

            // When/Then
            assertThrows(NullPointerException.class, () -> {
                validator.isValid(user, context);
            }, "Both null passwords should throw NullPointerException");
        }
    }

    @DisplayName("Whitespace And Special Cases")
    @Nested
    class WhitespaceAndSpecialCasesTests {
        @Test
        @DisplayName("Should return false when password contains only whitespace")
        void testPasswordWithOnlyWhitespace() {
            // Given
            UserRegistrationDto user = new UserRegistrationDto();
            user.setPassword("        ");
            user.setMatchingPassword("        ");

            // When
            boolean result = validator.isValid(user, context);

            // Then
            assertFalse(result, "Password with only whitespace should fail validation");
        }

        @Test
        @DisplayName("Should validate password with whitespace if it meets all requirements")
        void testPasswordWithWhitespace() {
            // Given - password with space in the middle
            UserRegistrationDto user = new UserRegistrationDto();
            user.setPassword("Valid Pass123@");
            user.setMatchingPassword("Valid Pass123@");

            // When
            boolean result = validator.isValid(user, context);

            // Then
            assertTrue(result, "Password with whitespace should pass if it meets all requirements");
        }

        @Test
        @DisplayName("Should return false when passwords differ only in whitespace")
        void testPasswordsDifferInWhitespace() {
            // Given
            UserRegistrationDto user = new UserRegistrationDto();
            user.setPassword("ValidPass123@");
            user.setMatchingPassword("ValidPass123@ ");

            // When
            boolean result = validator.isValid(user, context);

            // Then
            assertFalse(result, "Passwords differing in whitespace should fail validation");
        }
    }
}
