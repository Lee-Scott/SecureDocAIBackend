package com.familyFirstSoftware.SecureDocAIBackend.dtorequest.questionnaire;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * Request DTO for submitting questionnaire responses
 */
@Data
public class SubmitQuestionnaireResponseRequest {

    @NotBlank(message = "Questionnaire ID is required")
    private String questionnaireId;

    private String userId; // Will be set from authentication context if null

    @NotNull(message = "Completion status is required")
    private Boolean isCompleted;

    private Double totalScore;

    @Valid
    private List<SubmitQuestionResponseRequest> responses;
}
