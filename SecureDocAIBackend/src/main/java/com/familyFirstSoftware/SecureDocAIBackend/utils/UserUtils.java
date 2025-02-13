package com.familyFirstSoftware.SecureDocAIBackend.utils;

import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.entity.ConfirmationEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.RoleEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.NINETY_DAYS;
import static org.apache.logging.log4j.util.Strings.EMPTY;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/14/2024
 *
 * Todo: mapping could probably use a library
 */

public class UserUtils {
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

    public static User fromUserEntity(UserEntity userEntity, RoleEntity role, ConfirmationEntity credentialEntity) {
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

    private static boolean isCredentialNonExpired(ConfirmationEntity credentialEntity) {
        return credentialEntity.getUpdatedAt().plusDays(NINETY_DAYS).isAfter(LocalDateTime.now());
    }


}

