package com.familyFirstSoftware.SecureDocAIBackend.dtorequest.questionnaire;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * Request DTO for submitting individual question responses
 */
@Data
public class SubmitQuestionResponseRequest {

    @NotBlank(message = "Question ID is required")
    private String questionId;

    private String answerValue;

    private Boolean isSkipped = false;
}
