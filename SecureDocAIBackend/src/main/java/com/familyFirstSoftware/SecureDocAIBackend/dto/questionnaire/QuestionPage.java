package com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Builder;

import java.util.List;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * DTO for questionnaire page responses
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionPage {
    private String id;
    private String questionnaireId;
    private Integer pageNumber;
    private String title;
    private String description;
    private Boolean isRequired;
    private List<Question> questions;
}
