package com.familyFirstSoftware.SecureDocAIBackend.dtorequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 2/27/2025
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordRequest {
    @NotEmpty(message = "User ID is required")
    private String userId;
    @NotEmpty(message = "New Password name is required")
    private String newPassword;
    @NotEmpty(message = "Confirm New Password name is required")
    private String confirmNewPassword;
}



