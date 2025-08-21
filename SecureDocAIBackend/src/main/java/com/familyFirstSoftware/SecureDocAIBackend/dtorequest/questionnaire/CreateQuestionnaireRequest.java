package com.familyFirstSoftware.SecureDocAIBackend.dtorequest.questionnaire;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * Request DTO for creating questionnaires
 */
@Data
public class CreateQuestionnaireRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Estimated time is required")
    @Positive(message = "Estimated time must be positive")
    private Integer estimatedTimeMinutes;

    private Boolean isActive = true;

    @Valid
    private List<CreateQuestionPageRequest> pages;
}
