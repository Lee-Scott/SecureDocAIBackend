package com.familyFirstSoftware.SecureDocAIBackend.mapper;

import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.entity.CredentialEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.RoleEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.UUID;


import static org.apache.logging.log4j.util.Strings.EMPTY;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 2/22/2025
 */

public class UserMapper {

    public static UserEntity toUserEntity(User user, RoleEntity role) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        // Convert String dates back to LocalDateTime
        userEntity.setLastLogin(LocalDateTime.parse(user.getLastLogin()));
        userEntity.setCreatedAt(LocalDateTime.parse(user.getCreatedAt()));
        userEntity.setUpdatedAt(LocalDateTime.parse(user.getUpdatedAt()));

        // Set role
        userEntity.setRole(role);

        return userEntity;
    }

    public static User fromUserEntity(UserEntity userEntity, RoleEntity role, CredentialEntity credentialEntity) {
        User user = new User();
        BeanUtils.copyProperties(userEntity, user);
        user.setLastLogin(userEntity.getLastLogin().toString());
        user.setCredentialsNonExpired(userEntity.isAccountNonExpired());
        user.setCreatedAt(userEntity.getCreatedAt().toString());
        user.setUpdatedAt(userEntity.getUpdatedAt().toString());
        user.setRole(role.getName());
        user.setAuthorities(role.getAuthorities().getValue());
        return user;
    }



    public static UserEntity createUserEntity(String firstName, String lastName, String email, RoleEntity role) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .lastLogin(LocalDateTime.now())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .mfa(false)
                .enabled(true)
                .loginAttempts(0)
                .qrCodeSecret(EMPTY)
                .phone(EMPTY)
                .bio(EMPTY)
                .imageUrl("https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y")
                .role(role)
                .build();

    }
}