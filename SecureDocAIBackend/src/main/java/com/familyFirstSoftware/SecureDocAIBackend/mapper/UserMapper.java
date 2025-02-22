package com.familyFirstSoftware.SecureDocAIBackend.mapper;

import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.entity.CredentialEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.RoleEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

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
        userEntity.setUpdatedAt(LocalDateTime.parse(user.getUpdateAt()));

        // Set role
        userEntity.setRole(role);

        return userEntity;
    }

    public static User fromUserEntity(UserEntity userEntity, RoleEntity role, CredentialEntity credentialEntity) {
        User user = new User();
        BeanUtils.copyProperties(userEntity, user);
        user.setLastLogin(userEntity.getLastLogin().toString());
        user.setCreditNonExpired(isCredentialNonExpired(credentialEntity));
        user.setCreatedAt(userEntity.getCreatedAt().toString());
        user.setUpdateAt(userEntity.getUpdatedAt().toString());
        user.setRole(role.getName());
        user.setAuthorities(role.getAuthorities().getValue());
        return user;
    }

    private static boolean isCredentialNonExpired(CredentialEntity credentialEntity) {
        // TODO: Implement your logic to check if the credential is non-expired
        return true; // Placeholder implementation, replace with actual logic
    }
}