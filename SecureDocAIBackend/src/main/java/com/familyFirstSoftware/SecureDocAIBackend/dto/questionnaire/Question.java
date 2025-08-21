package com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * DTO for question responses
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Question {
    private String id;
    private String pageId;
    private Integer questionNumber;
    private String questionText;
    private String questionType;
    private Boolean isRequired;
    private String helpText;
    private Map<String, String> validationRules;
    private List<QuestionOption> options;
}
