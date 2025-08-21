package com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * DTO for questionnaire response
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionnaireResponse {
    private String id;
    private String questionnaireId;
    private String userId;
    private Boolean isCompleted;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastModifiedAt;
    private Double totalScore;
    private List<QuestionResponse> responses;
    private List<CategoryScore> categoryScores;
}
