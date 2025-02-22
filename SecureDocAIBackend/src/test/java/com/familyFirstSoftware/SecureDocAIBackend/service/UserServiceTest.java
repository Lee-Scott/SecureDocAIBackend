package com.familyFirstSoftware.SecureDocAIBackend.service;

import com.familyFirstSoftware.SecureDocAIBackend.cache.CacheStore;
import com.familyFirstSoftware.SecureDocAIBackend.entity.ConfirmationEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.CredentialEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.RoleEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.Authority;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.LoginType;
import com.familyFirstSoftware.SecureDocAIBackend.event.UserEvent;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.familyFirstSoftware.SecureDocAIBackend.mapper.UserMapper;
import com.familyFirstSoftware.SecureDocAIBackend.repository.ConfirmationRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.CredentialRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.RoleRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.UserRepository;
import com.familyFirstSoftware.SecureDocAIBackend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 2/13/2025
 *
 * Todo: more unit tests + integration tests and end to end tests
 */

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private CredentialRepository credentialRepository;
    @Mock
    private ConfirmationRepository confirmationRepository;
    @Mock
    private BCryptPasswordEncoder encoder;
    @Mock
    private CacheStore<String, Integer> userCache;
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    @DisplayName("Test create user")
    public void createUserTest() {
        // Arrange
        var userEntity = new UserEntity();
        var credentialEntity = new CredentialEntity(userEntity, "encodedPassword");
        var confirmationEntity = new ConfirmationEntity(userEntity);
        var roleEntity = new RoleEntity("USER", Authority.USER);

        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(credentialRepository.save(any(CredentialEntity.class))).thenReturn(credentialEntity);
        when(confirmationRepository.save(any(ConfirmationEntity.class))).thenReturn(confirmationEntity);
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(roleEntity));

        // Act
        userServiceImpl.createUser("John", "Doe", "john.doe@example.com", "password");

        // Assert
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(credentialRepository, times(1)).save(any(CredentialEntity.class));
        verify(confirmationRepository, times(1)).save(any(ConfirmationEntity.class));
        verify(publisher, times(1)).publishEvent(any(UserEvent.class));
    }

    @Test
    @DisplayName("Test get role by name")
    public void getRoleNameTest() {
        // Arrange
        var roleEntity = new RoleEntity("USER", Authority.USER);
        when(roleRepository.findByNameIgnoreCase("USER")).thenReturn(Optional.of(roleEntity));

        // Act
        var result = userServiceImpl.getRoleName("USER");

        // Assert
        assertThat(result).isEqualTo(roleEntity);
    }

    @Test
    @DisplayName("Test get role by name - not found")
    public void getRoleNameNotFoundTest() {
        // Arrange
        when(roleRepository.findByNameIgnoreCase("USER")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ApiException.class, () -> userServiceImpl.getRoleName("USER"));
    }

    @Test
    @DisplayName("Test verify account key")
    public void verifyAccountKeyTest() {
        // Arrange
        var userEntity = new UserEntity();
        var confirmationEntity = new ConfirmationEntity(userEntity);
        when(confirmationRepository.findByKey("key")).thenReturn(Optional.of(confirmationEntity));
        when(userRepository.findUserByEmailIgnoreCase("john.doe@example.com")).thenReturn(Optional.of(userEntity));
        userEntity.setEmail("john.doe@example.com");

        // Act
        userServiceImpl.verifyAccountKey("key");

        // Assert
        verify(userRepository, times(1)).save(userEntity);
        verify(confirmationRepository, times(1)).delete(confirmationEntity);
    }

    @Test
    @DisplayName("Test update login attempt")
    public void updateLoginAttemptTest() {
        // Arrange
        var userEntity = new UserEntity();
        userEntity.setEmail("john.doe@example.com");
        when(userRepository.findUserByEmailIgnoreCase("john.doe@example.com")).thenReturn(Optional.of(userEntity));
        when(userCache.get("john.doe@example.com")).thenReturn(0);

        // Act
        userServiceImpl.updateLoginAttempt("john.doe@example.com", LoginType.LOGIN_ATTEMPT);

        // Assert
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Test get user by user ID")
    public void getUserByUserIdTest() {
        // Arrange
        var userEntity = new UserEntity();
        userEntity.setFirstName("John");
        userEntity.setId(1L);
        userEntity.setUserId("1");
        userEntity.setCreatedAt(LocalDateTime.of(1991, 9, 24, 0, 0, 0, 0));
        userEntity.setUpdatedAt(LocalDateTime.of(1991, 9, 24, 0, 0, 0, 0));
        userEntity.setLastLogin(LocalDateTime.of(1991, 9, 24, 0, 0, 0, 0));

        var roleEntity = new RoleEntity("USER", Authority.USER);
        userEntity.setRole(roleEntity);

        var credentialEntity = new CredentialEntity();
        credentialEntity.setUpdatedAt(LocalDateTime.of(2024, 12, 24, 0, 0, 0, 0));
        credentialEntity.setPassword("password");
        credentialEntity.setUserEntity(userEntity);

        when(userRepository.findUserByUserId("1")).thenReturn(Optional.of(userEntity));
        when(credentialRepository.getCredentialByUserEntityId(1L)).thenReturn(Optional.of(credentialEntity));

        // Act
        var userByUserId = userServiceImpl.getUserByUserId("1");

        // Assert
        assertThat(userByUserId.getFirstName()).isEqualTo("John");
        assertThat(userByUserId.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Test get user by email")
    public void getUserByEmailTest() {
        // Arrange
        var userEntity = new UserEntity();
        userEntity.setEmail("john.doe@example.com");
        var credentialEntity = new CredentialEntity();
        when(userRepository.findUserByEmailIgnoreCase("john.doe@example.com")).thenReturn(Optional.of(userEntity));
        when(credentialRepository.getCredentialByUserEntityId(anyLong())).thenReturn(Optional.of(credentialEntity));

        // Act
        var userByEmail = userServiceImpl.getUserByEmail("john.doe@example.com");

        // Assert
        assertThat(userByEmail.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Test get user credential by ID")
    public void getUserCredentialByIdTest() {
        // Arrange
        var credentialEntity = new CredentialEntity();
        when(credentialRepository.getCredentialByUserEntityId(1L)).thenReturn(Optional.of(credentialEntity));

        // Act
        var result = userServiceImpl.getUserCredentialById(1L);

        // Assert
        assertThat(result).isEqualTo(credentialEntity);
    }

    @Test
    @DisplayName("Test get user credential by ID - not found")
    public void getUserCredentialByIdNotFoundTest() {
        // Arrange
        when(credentialRepository.getCredentialByUserEntityId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ApiException.class, () -> userServiceImpl.getUserCredentialById(1L));
    }

    @Test
    @DisplayName("Test set up MFA")
    public void setUpMfaTest() {
        // Arrange
        var userEntity = new UserEntity();
        userEntity.setEmail("john.doe@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(credentialRepository.getCredentialByUserEntityId(1L)).thenReturn(Optional.of(new CredentialEntity()));

        // Act
        var result = userServiceImpl.setUpMfa(1L);

        // Assert
        UserEntity resultUserEntity = UserMapper.toUserEntity(result, userEntity.getRole());
        assertThat(resultUserEntity.isMfa()).isTrue();
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Test cancel MFA")
    public void cancelMfaTest() {
        // Arrange
        var userEntity = new UserEntity();
        userEntity.setMfa(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(credentialRepository.getCredentialByUserEntityId(1L)).thenReturn(Optional.of(new CredentialEntity()));

        // Act
        var result = userServiceImpl.cancelMfa(1L);

        // Assert
        UserEntity resultUserEntity = UserMapper.toUserEntity(result, userEntity.getRole());
        assertThat(resultUserEntity.isMfa()).isFalse();
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Test verify QR code")
    public void verifyQrCodeTest() {
        // Arrange
        var userEntity = new UserEntity();
        userEntity.setQrCodeSecret("secret");
        when(userRepository.findUserByUserId("1")).thenReturn(Optional.of(userEntity));

        // Act
        var result = userServiceImpl.verifyQrCode("1", "123456");

        // Assert
        assertThat(result).isNotNull();
        verify(userRepository, times(1)).findUserByUserId("1");
    }

    @Test
    @DisplayName("Test verify QR code - invalid")
    public void verifyQrCodeInvalidTest() {
        // Arrange
        var userEntity = new UserEntity();
        userEntity.setQrCodeSecret("secret");
        when(userRepository.findUserByUserId("1")).thenReturn(Optional.of(userEntity));

        // Act & Assert
        assertThrows(ApiException.class, () -> userServiceImpl.verifyQrCode("1", "invalid"));
    }
}