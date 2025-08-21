package com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * DTO for individual question response
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionResponse {
    private String id;
    private String questionId;
    private String questionText;
    private String answerValue;
    private Boolean isSkipped;
    private LocalDateTime respondedAt;
}
