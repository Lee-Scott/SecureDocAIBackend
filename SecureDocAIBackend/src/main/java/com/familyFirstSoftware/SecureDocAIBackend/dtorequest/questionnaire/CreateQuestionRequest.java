package com.familyFirstSoftware.SecureDocAIBackend.dtorequest.questionnaire;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * Request DTO for creating questions
 */
@Data
public class CreateQuestionRequest {

    @NotNull(message = "Question number is required")
    @Positive(message = "Question number must be positive")
    private Integer questionNumber;

    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotBlank(message = "Question type is required")
    private String questionType;

    private Boolean isRequired = true;

    private String helpText;

    private Map<String, String> validationRules;

    @Valid
    private List<CreateQuestionOptionRequest> options;
}
