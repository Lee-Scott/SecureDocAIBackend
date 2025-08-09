package com.familyFirstSoftware.SecureDocAIBackend.service;

import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.familyFirstSoftware.SecureDocAIBackend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit test template for a hypothetical AuthService.
 *
 * This class serves as a blueprint for testing authentication and authorization logic.
 * It demonstrates various testing techniques, including parameterized tests for validation.
 *
 * Naming Convention:
 * methodName_Scenario_ExpectedBehavior
 * e.g., loginUser_WithValidCredentials_ReturnsAuthToken
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // --- Mocks and Service Under Test ---

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // Hypothetical service that doesn't exist yet.
    // When created, it would be injected here.
    // @InjectMocks
    // private AuthServiceImpl authService;


    // --- Test Cases ---

    @Test
    @DisplayName("Login - Happy Path with Valid Credentials")
    void login_WithValidCredentials_ReturnsToken() {
        // Arrange
        String email = "test@example.com";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";
        UserEntity mockUser = TestDataFactory.createUser(email, encodedPassword, true);

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        // when(jwtService.generateToken(mockUser)).thenReturn("mock-jwt-token"); // Assuming a JWT service

        // Act
        // String token = authService.login(email, rawPassword);

        // Assert
        // assertThat(token).isEqualTo("mock-jwt-token");
        // verify(userRepository).save(mockUser); // e.g., to update last login timestamp
    }

    @Test
    @DisplayName("Login - User Not Found")
    void login_WhenUserNotFound_ThrowsApiException() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.empty());

        // Act & Assert
        // assertThatThrownBy(() -> authService.login(email, "password"))
        //         .isInstanceOf(ApiException.class)
        //         .hasMessage("Invalid credentials");
    }

    @Test
    @DisplayName("Login - Incorrect Password")
    void login_WithInvalidPassword_ThrowsApiException() {
        // Arrange
        String email = "test@example.com";
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedPassword";
        UserEntity mockUser = TestDataFactory.createUser(email, encodedPassword, true);

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // Act & Assert
        // assertThatThrownBy(() -> authService.login(email, rawPassword))
        //         .isInstanceOf(ApiException.class)
        //         .hasMessage("Invalid credentials");

        // --- verifyNoMoreInteractions Example ---
        // Use this to ensure that after the failed password check, no further
        // interactions (like generating a token or saving the user) occurred.
        // verify(jwtService, never()).generateToken(any());
        // verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Login - User Account Locked")
    void login_WhenAccountIsLocked_ThrowsApiException() {
        // Arrange
        String email = "locked@example.com";
        UserEntity lockedUser = TestDataFactory.createUser(email, "password", false);
        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(lockedUser));

        // Act & Assert
        // assertThatThrownBy(() -> authService.login(email, "password"))
        //         .isInstanceOf(ApiException.class)
        //         .hasMessage("Account is locked");
    }

    /**
     * Parameterized tests are excellent for testing validation logic.
     * They allow you to run the same test with multiple inputs to cover various edge cases
     * without writing repetitive test methods.
     */
    @ParameterizedTest
    @CsvSource({
            "test, password123, 'Invalid email format'", // Invalid email
            "test@example.com, short, 'Password must be at least 8 characters'", // Short password
            "'', password123, 'Email cannot be empty'", // Empty email
            "test@example.com, '', 'Password cannot be empty'" // Empty password
    })
    @DisplayName("Register - With Invalid Inputs")
    void register_WithInvalidInputs_ThrowsValidationException(String email, String password, String expectedMessage) {
        // Act & Assert
        // assertThatThrownBy(() -> authService.register(email, password))
        //         .isInstanceOf(IllegalArgumentException.class)
        //         .hasMessage(expectedMessage);
    }


    // --- Test Data Factory ---
    // A dedicated factory class is another great way to manage test data,
    // especially if the data is used across multiple test classes.
    static class TestDataFactory {
        static UserEntity createUser(String email, String password, boolean isEnabled) {
            UserEntity user = new UserEntity();
            user.setEmail(email);
            // In a real service, the password would be handled by a CredentialEntity
            // user.getCredential().setPassword(password);
            user.setEnabled(isEnabled);
            user.setAccountNonLocked(true);
            user.setAccountNonExpired(true);
            return user;
        }
    }
}
