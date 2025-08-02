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
 * Request DTO for creating question pages
 */
@Data
public class CreateQuestionPageRequest {

    @NotNull(message = "Page number is required")
    @Positive(message = "Page number must be positive")
    private Integer pageNumber;

    @NotBlank(message = "Page title is required")
    private String title;

    private String description;

    private Boolean isRequired = true;

    @Valid
    private List<CreateQuestionRequest> questions;
}
