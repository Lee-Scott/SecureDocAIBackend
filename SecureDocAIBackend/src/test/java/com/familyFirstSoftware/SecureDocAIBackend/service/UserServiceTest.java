package com.familyFirstSoftware.SecureDocAIBackend.service;

import com.familyFirstSoftware.SecureDocAIBackend.entity.RoleEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import com.familyFirstSoftware.SecureDocAIBackend.repository.ConfirmationRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.CredentialRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.RoleRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.UserRepository;
import com.familyFirstSoftware.SecureDocAIBackend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
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
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser() {
        when(roleRepository.findByNameIgnoreCase(any())).thenReturn(Optional.of(new RoleEntity()));
        userService.createUser("test", "user", "test@test.com", "password");
        verify(userRepository).save(any(UserEntity.class));
        verify(credentialRepository).save(any());
        verify(confirmationRepository).save(any());
        verify(publisher).publishEvent(any());
    }

    @Test
    public void testUpdateUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@test.com");
        when(userRepository.findWithRoleByUserId(any())).thenReturn(Optional.of(userEntity));
        userService.updateUser("1", "test", "user", "new@test.com", "1234567890", "bio");
        verify(userRepository).save(any(UserEntity.class));
    }
}