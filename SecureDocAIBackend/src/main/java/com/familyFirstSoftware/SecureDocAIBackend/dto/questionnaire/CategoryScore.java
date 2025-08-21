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
 * DTO for category score in questionnaire analytics
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryScore {
    private String category;
    private Double score;
    private Double maxScore;
    private Double percentage;
}
