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
import com.familyFirstSoftware.SecureDocAIBackend.repository.ConfirmationRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.CredentialRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.RoleRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.UserRepository;
import com.familyFirstSoftware.SecureDocAIBackend.service.impl.UserServiceImpl;
import com.familyFirstSoftware.SecureDocAIBackend.utils.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import dev.samstevens.totp.exceptions.CodeGenerationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @Test
    @DisplayName("Test set up MFA")
    public void setUpMfaTest() {
        // Arrange
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(credentialRepository.getCredentialByUserEntityId(userEntity.getId())).thenReturn(Optional.of(new CredentialEntity()));

        // Act
        var result = userServiceImpl.setUpMfa(userEntity.getId());

        // Assert
        UserEntity resultUserEntity = UserUtils.toUserEntity(result, userEntity.getRole());
        assertThat(resultUserEntity.isMfa()).isTrue();
        verify(userRepository, times(1)).save(userEntity);
    }


    @Test
    @DisplayName("Test cancel MFA")
    public void cancelMfaTest() {
        // Arrange
        var userEntity = new UserEntity();
        userEntity.setMfa(true);
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(credentialRepository.getCredentialByUserEntityId(userId)).thenReturn(Optional.of(new CredentialEntity()));

        // Act
        var result = userServiceImpl.cancelMfa(userId);

        

        // Assert
        UserEntity resultUserEntity = UserUtils.toUserEntity(result, userEntity.getRole());
        assertThat(resultUserEntity.isMfa()).isFalse();
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Test verify QR code")
    public void verifyQrCodeTest() throws CodeGenerationException {
        // Arrange
        // Use the fully initialized user from setup
        var secret = new dev.samstevens.totp.secret.DefaultSecretGenerator().generate();
        var totp = new dev.samstevens.totp.qr.QrData.Builder()
                .label(userEntity.getEmail())
                .secret(secret)
                .issuer("SecureDocAI")
                .build();
        var code = new dev.samstevens.totp.code.DefaultCodeGenerator().generate(secret,
                System.currentTimeMillis() / 30000);

        userEntity.setQrCodeSecret(secret);
        userEntity.setRole(new RoleEntity("USER", Authority.USER)); // Ensure role is set

        when(userRepository.findUserByUserId(userEntity.getUserId())).thenReturn(Optional.of(userEntity));
        when(credentialRepository.getCredentialByUserEntityId(userEntity.getId())).thenReturn(Optional.of(new CredentialEntity()));

        // Act
        var result = userServiceImpl.verifyQrCode(userEntity.getUserId(), code);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isMfa()).isTrue(); // The user DTO should reflect that MFA is verified
        verify(userRepository, times(1)).findUserByUserId(userEntity.getUserId());
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