package com.familyFirstSoftware.SecureDocAIBackend.dtorequest.questionnaire;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * Request DTO for creating question options
 */
@Data
public class CreateQuestionOptionRequest {

    @NotBlank(message = "Option text is required")
    private String optionText;

    @NotBlank(message = "Option value is required")
    private String optionValue;

    @NotNull(message = "Order index is required")
    @Positive(message = "Order index must be positive")
    private Integer orderIndex;
}
