package com.familyFirstSoftware.SecureDocAIBackend.utils;

import com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire.*;
import com.familyFirstSoftware.SecureDocAIBackend.entity.questionnaire.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * Utility class for mapping between questionnaire entities and DTOs
 */
@Component
public class QuestionnaireDtoMapper {

    public Questionnaire toDto(QuestionnaireEntity entity) {
        if (entity == null) return null;

        return Questionnaire.builder()
                .id(entity.getReferenceId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .category(entity.getCategory().name())
                .isActive(entity.getIsActive())
                .totalQuestions(entity.getTotalQuestions())
                .estimatedTimeMinutes(entity.getEstimatedTimeMinutes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreator() != null ? entity.getCreator().getReferenceId() : null)
                .pages(entity.getPages() != null ?
                       entity.getPages().stream().map(this::toDto).collect(Collectors.toList()) : null)
                .build();
    }

    public QuestionPage toDto(QuestionPageEntity entity) {
        if (entity == null) return null;

        return QuestionPage.builder()
                .id(entity.getReferenceId())
                .questionnaireId(entity.getQuestionnaire().getReferenceId())
                .pageNumber(entity.getPageNumber())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .isRequired(entity.getIsRequired())
                .questions(entity.getQuestions() != null ?
                          entity.getQuestions().stream().map(this::toDto).collect(Collectors.toList()) : null)
                .build();
    }

    public Question toDto(QuestionEntity entity) {
        if (entity == null) return null;

        return Question.builder()
                .id(entity.getReferenceId())
                .pageId(entity.getPage().getReferenceId())
                .questionNumber(entity.getQuestionNumber())
                .questionText(entity.getQuestionText())
                .questionType(entity.getQuestionType().name())
                .isRequired(entity.getIsRequired())
                .helpText(entity.getHelpText())
                .validationRules(entity.getValidationRules())
                .options(entity.getOptions() != null ?
                        entity.getOptions().stream().map(this::toDto).collect(Collectors.toList()) : null)
                .build();
    }

    public QuestionOption toDto(QuestionOptionEntity entity) {
        if (entity == null) return null;

        return QuestionOption.builder()
                .id(entity.getReferenceId())
                .questionId(entity.getQuestion().getReferenceId())
                .optionText(entity.getOptionText())
                .optionValue(entity.getOptionValue())
                .orderIndex(entity.getOrderIndex())
                .build();
    }

    public QuestionnaireResponse toDto(QuestionnaireResponseEntity entity) {
        if (entity == null) return null;

        return QuestionnaireResponse.builder()
                .id(entity.getReferenceId())
                .questionnaireId(entity.getQuestionnaire().getReferenceId())
                .userId(entity.getUser().getReferenceId())
                .isCompleted(entity.getIsCompleted())
                .startedAt(entity.getStartedAt())
                .completedAt(entity.getCompletedAt())
                .lastModifiedAt(entity.getLastModifiedAt())
                .totalScore(entity.getTotalScore())
                .responses(entity.getResponses() != null ?
                          entity.getResponses().stream().map(this::toDto).collect(Collectors.toList()) : null)
                .build();
    }

    public QuestionResponse toDto(QuestionResponseEntity entity) {
        if (entity == null) return null;

        return QuestionResponse.builder()
                .id(entity.getReferenceId())
                .questionId(entity.getQuestion().getReferenceId())
                .questionText(entity.getQuestion().getQuestionText())
                .answerValue(entity.getAnswerValue())
                .isSkipped(entity.getIsSkipped())
                .respondedAt(entity.getRespondedAt())
                .build();
    }

    public List<Questionnaire> toDtoList(List<QuestionnaireEntity> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }
}
