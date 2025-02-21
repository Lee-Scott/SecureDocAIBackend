package com.familyFirstSoftware.SecureDocAIBackend.service;

import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.entity.CredentialEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.RoleEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.Authority;
import com.familyFirstSoftware.SecureDocAIBackend.repository.CredentialRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

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
    private CredentialRepository credentialRepository;
    @InjectMocks // injects mock objects into UserServiceImpl
    private UserServiceImpl userServiceImpl;


    // Todo: solid principles make single responsibility by pulling out the creation of userEntity ect
    // Todo: run with coverage get to 100%
    @Test
    @DisplayName("Test find user by ID")
    public void getUserByUserIdTest() {
        // Arrange / Given
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

        when(userRepository.findUserByUserId("1L")).thenReturn(Optional.of(userEntity));
        when(credentialRepository.getCredentialByUserEntityId(1L)).thenReturn(Optional.of(credentialEntity));

        // Act / When
        var userByUserId = userServiceImpl.getUserByUserId("1");

        // Assert / Then
        assertThat(userByUserId.getFirstName()).isEqualTo("John");
        assertThat(userByUserId.getId()).isEqualTo(1L);



    }


}

