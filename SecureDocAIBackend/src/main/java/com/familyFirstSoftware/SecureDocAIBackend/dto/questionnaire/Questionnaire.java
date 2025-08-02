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
 * DTO for questionnaire responses
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Questionnaire {
    private String id;
    private String title;
    private String description;
    private String category;
    private Boolean isActive;
    private Integer totalQuestions;
    private Integer estimatedTimeMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private List<QuestionPage> pages;
}
