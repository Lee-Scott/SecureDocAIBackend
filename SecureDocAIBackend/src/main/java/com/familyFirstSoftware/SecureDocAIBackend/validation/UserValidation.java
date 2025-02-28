package com.familyFirstSoftware.SecureDocAIBackend.validation;

import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 2/27/2025
 *
 * Todo: add more validation
 */

@Slf4j
public class UserValidation {

    public static void verifyAccountStatus(UserEntity userEntity) {
        if(!userEntity.isEnabled()) {
            throw new ApiException("User is disabled");
        }
        if(!userEntity.isAccountNonLocked()) {
            throw new ApiException("User is locked");
        }
        if(!userEntity.isAccountNonExpired()) {
            throw new ApiException("User is expired");
        }
    }

}

