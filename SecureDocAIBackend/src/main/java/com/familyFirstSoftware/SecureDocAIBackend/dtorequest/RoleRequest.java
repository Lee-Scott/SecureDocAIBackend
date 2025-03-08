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
 * @since 3/8/2025
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleRequest {
    @NotEmpty(message = "Role is required")
    private String role;

}

