package com.familyFirstSoftware.SecureDocAIBackend.service;

import com.familyFirstSoftware.SecureDocAIBackend.cache.CacheStore;
import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.entity.ConfirmationEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.CredentialEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.RoleEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;

import com.familyFirstSoftware.SecureDocAIBackend.enumeration.Authority;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.LoginType;
import com.familyFirstSoftware.SecureDocAIBackend.event.UserEvent;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.familyFirstSoftware.SecureDocAIBackend.repository.ConfirmationRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.CredentialRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.RoleRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.UserRepository;
import com.familyFirstSoftware.SecureDocAIBackend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
    private CacheStore<String, User> userByUserIdCache;
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private UserEntity userEntity;

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
        when(userRepository.findWithRoleByEmailIgnoreCase("john.doe@example.com")).thenReturn(Optional.of(userEntity));
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
        when(userRepository.findWithRoleByEmailIgnoreCase("john.doe@example.com")).thenReturn(Optional.of(userEntity));

        // Act
        userServiceImpl.updateLoginAttempt("john.doe@example.com", LoginType.LOGIN_ATTEMPT);

        // Assert
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Test get user by user ID")
    public void getUserByUserIdTest() {
        // Arrange
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUserId(userId.toString());
        
        // Mock RoleEntity to prevent NullPointerException
        RoleEntity mockRole = mock(RoleEntity.class);
        when(mockRole.getName()).thenReturn("USER");
        when(mockRole.getAuthorities()).thenReturn(Authority.USER);
        userEntity.setRole(mockRole);
        
        CredentialEntity mockCredential = mock(CredentialEntity.class);
        when(mockCredential.getUpdatedAt()).thenReturn(LocalDateTime.now());

        when(userRepository.findWithRoleByUserId(userId.toString())).thenReturn(Optional.of(userEntity));
        when(credentialRepository.getCredentialByUserEntityId(anyLong())).thenReturn(Optional.of(mockCredential));

        // Act
        var result = userServiceImpl.getUserByUserId(userId.toString());

        // Assert
        assertThat(result.getId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Test get user credential by ID")
    public void getUserCredentialByIdTest() {
        // Arrange
        Long userId = 1L;
        CredentialEntity mockCredential = mock(CredentialEntity.class);

        when(credentialRepository.getCredentialByUserEntityId(userId)).thenReturn(Optional.of(mockCredential));

        // Act
        var result = userServiceImpl.getUserCredentialById(userId);

        // Assert
        assertThat(result).isEqualTo(mockCredential);
    }

    @Test
    @DisplayName("Test get user credential by ID - not found")
    public void getUserCredentialByIdNotFoundTest() {
        // Arrange
        when(credentialRepository.getCredentialByUserEntityId(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ApiException.class, () -> userServiceImpl.getUserCredentialById(1L));
    }

    @Test
    @DisplayName("Test set up MFA")
    public void setUpMfaTest() {
        // Arrange
        Long userId = 1L;
        UserEntity mockUser = mock(UserEntity.class);
        CredentialEntity mockCredential = mock(CredentialEntity.class);
        
        // Mock RoleEntity to prevent NullPointerException
        RoleEntity mockRole = mock(RoleEntity.class);
        when(mockRole.getName()).thenReturn("USER");
        when(mockRole.getAuthorities()).thenReturn(Authority.USER);
        when(mockUser.getRole()).thenReturn(mockRole);
        when(mockUser.getId()).thenReturn(userId);
        
        // Stub CredentialEntity methods
        when(mockCredential.getUpdatedAt()).thenReturn(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(credentialRepository.getCredentialByUserEntityId(anyLong())).thenReturn(Optional.of(mockCredential));

        // Act
        var result = userServiceImpl.setUpMfa(userId);

        // Assert
        assertThat(result).isNotNull();
        verify(userRepository).save(mockUser);
    }

    @Test
    @DisplayName("Test cancel MFA")
    public void cancelMfaTest() {
        // Arrange
        Long userId = 1L;
        UserEntity mockUser = mock(UserEntity.class);
        CredentialEntity mockCredential = mock(CredentialEntity.class);
        
        // Mock RoleEntity to prevent NullPointerException
        RoleEntity mockRole = mock(RoleEntity.class);
        when(mockRole.getName()).thenReturn("USER");
        when(mockRole.getAuthorities()).thenReturn(Authority.USER);
        when(mockUser.getRole()).thenReturn(mockRole);
        when(mockUser.getId()).thenReturn(userId);
        
        // Stub CredentialEntity methods
        when(mockCredential.getUpdatedAt()).thenReturn(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(credentialRepository.getCredentialByUserEntityId(anyLong())).thenReturn(Optional.of(mockCredential));

        // Act
        var result = userServiceImpl.cancelMfa(userId);

        // Assert
        assertThat(result.isMfa()).isFalse();
        verify(userRepository).save(mockUser);
    }

    @BeforeEach
    public void setup() {
        userEntity = UserEntity.builder()
                .id(12345L)
                .userId("12345")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .bio("This is a test bio")
                .imageUrl("https://example.com/image.jpg")
                .loginAttempts(0)
                .lastLogin(LocalDateTime.now())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .enabled(true)
                .mfa(false)
                .build();
    }
}