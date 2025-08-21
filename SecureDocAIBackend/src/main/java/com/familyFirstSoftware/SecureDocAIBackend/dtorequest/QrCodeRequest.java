package com.familyFirstSoftware.SecureDocAIBackend.dtorequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 2/21/2025
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QrCodeRequest {

    @NotEmpty(message = "User is required")
    private String userId;
    @NotEmpty(message = "Qr Code is required")
    private String qrCode;
}
