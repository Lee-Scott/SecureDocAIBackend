package com.familyFirstSoftware.SecureDocAIBackend.utils;

import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
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
 * @since 12/14/2024
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

}

