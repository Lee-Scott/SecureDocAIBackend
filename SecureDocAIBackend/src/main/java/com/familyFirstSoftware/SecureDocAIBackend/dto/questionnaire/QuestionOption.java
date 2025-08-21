package com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Builder;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * DTO for question option responses
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionOption {
    private String id;
    private String questionId;
    private String optionText;
    private String optionValue;
    private Integer orderIndex;
}
