package com.familyFirstSoftware.SecureDocAIBackend.service.impl;

import com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire.Questionnaire;
import com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire.QuestionnaireResponse;
import com.familyFirstSoftware.SecureDocAIBackend.entity.questionnaire.*;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.questionnaire.QuestionnaireCategory;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.questionnaire.QuestionType;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.familyFirstSoftware.SecureDocAIBackend.repository.QuestionRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.QuestionnaireRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.QuestionnaireResponseRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.QuestionResponseRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.UserRepository;
import com.familyFirstSoftware.SecureDocAIBackend.service.QuestionnaireService;
import com.familyFirstSoftware.SecureDocAIBackend.utils.QuestionnaireDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * Service implementation for questionnaire operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionnaireServiceImpl implements QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;
    private final QuestionnaireResponseRepository responseRepository;
    private final QuestionResponseRepository questionResponseRepository;
    private final UserRepository userRepository;
    private final QuestionnaireDtoMapper dtoMapper;
    private final QuestionRepository questionRepository;

    @Override
    public Questionnaire createQuestionnaire(Questionnaire questionnaire) {
        log.info("Creating new questionnaire: {}", questionnaire.getTitle());

        var entity = new QuestionnaireEntity();
        entity.setTitle(questionnaire.getTitle());
        entity.setTitleSearch(questionnaire.getTitle() == null ? null : questionnaire.getTitle().toLowerCase());
        entity.setDescription(questionnaire.getDescription());
        entity.setCategory(QuestionnaireCategory.valueOf(questionnaire.getCategory()));
        entity.setIsActive(questionnaire.getIsActive() != null ? questionnaire.getIsActive() : true);
        entity.setEstimatedTimeMinutes(questionnaire.getEstimatedTimeMinutes());

        // Save questionnaire first to get ID
        var savedEntity = questionnaireRepository.save(entity);

        // Create pages if provided
        if (questionnaire.getPages() != null && !questionnaire.getPages().isEmpty()) {
            final var finalSavedEntity = savedEntity; // Make it effectively final for lambda
            var pages = questionnaire.getPages().stream()
                    .map(pageDto -> createPageEntity(pageDto, finalSavedEntity))
                    .collect(Collectors.toList());
            savedEntity.setPages(pages);
            savedEntity = questionnaireRepository.save(savedEntity);
        }

        log.info("Successfully created questionnaire with ID: {}", savedEntity.getReferenceId());
        return dtoMapper.toDto(savedEntity);
    }

    @Override
    public Questionnaire updateQuestionnaire(String questionnaireId, Questionnaire questionnaire) {
        log.info("Updating questionnaire: {}", questionnaireId);

        var entity = questionnaireRepository.findByReferenceId(questionnaireId)
                .orElseThrow(() -> new ApiException("Questionnaire not found with ID: " + questionnaireId));

        if (questionnaire.getTitle() != null) {
            entity.setTitle(questionnaire.getTitle());
            entity.setTitleSearch(questionnaire.getTitle().toLowerCase());
        }
        if (questionnaire.getDescription() != null) {
            entity.setDescription(questionnaire.getDescription());
        }
        if (questionnaire.getCategory() != null) {
            entity.setCategory(QuestionnaireCategory.valueOf(questionnaire.getCategory()));
        }
        if (questionnaire.getIsActive() != null) {
            entity.setIsActive(questionnaire.getIsActive());
        }
        if (questionnaire.getEstimatedTimeMinutes() != null) {
            entity.setEstimatedTimeMinutes(questionnaire.getEstimatedTimeMinutes());
        }

        var savedEntity = questionnaireRepository.save(entity);
        log.info("Successfully updated questionnaire: {}", questionnaireId);
        return dtoMapper.toDto(savedEntity);
    }

    @Override
    public void deleteQuestionnaire(String questionnaireId) {
        log.info("Deleting questionnaire: {}", questionnaireId);

        var entity = questionnaireRepository.findByReferenceId(questionnaireId)
                .orElseThrow(() -> new ApiException("Questionnaire not found with ID: " + questionnaireId));

        questionnaireRepository.delete(entity);
        log.info("Successfully deleted questionnaire: {}", questionnaireId);
    }

    @Override
    @Transactional(readOnly = true)
    public Questionnaire getQuestionnaireById(String questionnaireId) {
        log.debug("Fetching questionnaire: {}", questionnaireId);

        var entity = questionnaireRepository.findByReferenceId(questionnaireId)
                .orElseThrow(() -> new ApiException("Questionnaire not found with ID: " + questionnaireId));

        return dtoMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Questionnaire> getQuestionnaires(int page, int size, String category, String title) {
        log.debug("Fetching questionnaires - page: {}, size: {}, category: {}, title: {}",
                page, size, category, title);

        QuestionnaireCategory categoryEnum = null;
        if (category != null && !category.isEmpty()) {
            try {
                categoryEnum = QuestionnaireCategory.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid questionnaire category provided: {}", category);
                // Optionally, you could throw an exception or simply proceed with a null category
            }
        }

        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        String titleNormalized = (title == null || title.isEmpty()) ? null : "%" + title.toLowerCase() + "%";
        var entityPage = questionnaireRepository.findQuestionnairesWithFilters(categoryEnum, titleNormalized, pageable);

        var dtoList = entityPage.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Questionnaire> getActiveQuestionnaires() {
        log.debug("Fetching all active questionnaires");

        var pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("title"));
        var entities = questionnaireRepository.findByIsActiveTrue(pageable).getContent();

        return dtoMapper.toDtoList(entities);
    }

    @Override
    public QuestionnaireResponse submitResponse(QuestionnaireResponse response, com.familyFirstSoftware.SecureDocAIBackend.dto.User userDto) {
        log.info("Submitting questionnaire response for questionnaire: {} by user: {}", response.getQuestionnaireId(), userDto.getUserId());

        var questionnaire = questionnaireRepository.findByReferenceId(response.getQuestionnaireId())
                .orElseThrow(() -> new ApiException("Questionnaire not found with ID: " + response.getQuestionnaireId()));

        var user = userRepository.findByUserId(userDto.getUserId())
                .orElseThrow(() -> new ApiException("User not found with ID: " + userDto.getUserId()));

        // Check if response already exists
        var existingResponse = responseRepository.findByQuestionnaireIdAndUserId(questionnaire.getId(), user.getId());

        QuestionnaireResponseEntity responseEntity;
        if (existingResponse.isPresent()) {
            responseEntity = existingResponse.get();
            log.info("Updating existing response for user: {}", userDto.getUserId());
        } else {
            responseEntity = new QuestionnaireResponseEntity();
            responseEntity.setQuestionnaire(questionnaire);
            responseEntity.setUser(user);
            log.info("Creating new response for user: {}", userDto.getUserId());
        }

        responseEntity.setIsCompleted(response.getIsCompleted() != null ? response.getIsCompleted() : false);
        responseEntity.setTotalScore(response.getTotalScore());

        var savedResponse = responseRepository.save(responseEntity);

        // Save individual question responses
        if (response.getResponses() != null) {
            saveQuestionResponses(response.getResponses(), savedResponse);
        }

        log.info("Successfully submitted questionnaire response: {}", savedResponse.getReferenceId());
        return dtoMapper.toDto(savedResponse);
    }

    @Override
    public QuestionnaireResponse updateResponse(String responseId, QuestionnaireResponse response) {
        log.info("Updating questionnaire response: {}", responseId);

        var entity = responseRepository.findByReferenceId(responseId)
                .orElseThrow(() -> new ApiException("Response not found with ID: " + responseId));

        if (response.getIsCompleted() != null) {
            entity.setIsCompleted(response.getIsCompleted());
        }
        if (response.getTotalScore() != null) {
            entity.setTotalScore(response.getTotalScore());
        }

        var savedEntity = responseRepository.save(entity);

        // Save individual question responses if provided
        if (response.getResponses() != null && !response.getResponses().isEmpty()) {
            saveQuestionResponses(response.getResponses(), savedEntity);
        }

        log.info("Successfully updated response: {}", responseId);
        return dtoMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionnaireResponse getResponseById(String responseId) {
        log.debug("Fetching response: {}", responseId);

        var entity = responseRepository.findByReferenceId(responseId)
                .orElseThrow(() -> new ApiException("Response not found with ID: " + responseId));

        return dtoMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionnaireResponse> getUserResponses(String userId) {
        log.debug("Fetching responses for user: {}", userId);

        var user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException("User not found with ID: " + userId));

        var entities = responseRepository.findByUserId(user.getId());
        return entities.stream().map(dtoMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Object getQuestionnaireAnalytics(String questionnaireId) {
        log.debug("Generating analytics for questionnaire: {}", questionnaireId);

        var questionnaire = questionnaireRepository.findByReferenceId(questionnaireId)
                .orElseThrow(() -> new ApiException("Questionnaire not found with ID: " + questionnaireId));

        var totalResponses = responseRepository.countByQuestionnaireId(questionnaire.getId());
        var completedResponses = responseRepository.countCompletedByQuestionnaireId(questionnaire.getId());
        var avgCompletionTime = responseRepository.getAverageCompletionTimeMinutes(questionnaire.getId());

        // Build analytics response
        return java.util.Map.of(
                "questionnaireId", questionnaireId,
                "totalResponses", totalResponses,
                "completionRate", totalResponses > 0 ? (completedResponses * 100.0 / totalResponses) : 0.0,
                "averageCompletionTime", avgCompletionTime != null ? avgCompletionTime : 0.0
        );
    }

    private QuestionPageEntity createPageEntity(com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire.QuestionPage pageDto, QuestionnaireEntity questionnaire) {
        var pageEntity = new QuestionPageEntity();
        pageEntity.setQuestionnaire(questionnaire);
        pageEntity.setPageNumber(pageDto.getPageNumber());
        pageEntity.setTitle(pageDto.getTitle());
        pageEntity.setDescription(pageDto.getDescription());
        pageEntity.setIsRequired(pageDto.getIsRequired() != null ? pageDto.getIsRequired() : true);

        if (pageDto.getQuestions() != null && !pageDto.getQuestions().isEmpty()) {
            var questions = pageDto.getQuestions().stream()
                    .map(questionDto -> createQuestionEntity(questionDto, pageEntity))
                    .collect(Collectors.toList());
            pageEntity.setQuestions(questions);
        }

        return pageEntity;
    }

    private QuestionEntity createQuestionEntity(com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire.Question questionDto, QuestionPageEntity page) {
        var questionEntity = new QuestionEntity();
        questionEntity.setPage(page);
        questionEntity.setQuestionNumber(questionDto.getQuestionNumber());
        questionEntity.setQuestionText(questionDto.getQuestionText());
        questionEntity.setQuestionType(QuestionType.valueOf(questionDto.getQuestionType()));
        questionEntity.setIsRequired(questionDto.getIsRequired() != null ? questionDto.getIsRequired() : true);
        questionEntity.setHelpText(questionDto.getHelpText());
        questionEntity.setValidationRules(questionDto.getValidationRules());

        if (questionDto.getOptions() != null && !questionDto.getOptions().isEmpty()) {
            var options = questionDto.getOptions().stream()
                    .map(optionDto -> createOptionEntity(optionDto, questionEntity))
                    .collect(Collectors.toList());
            questionEntity.setOptions(options);
        }

        return questionEntity;
    }

    private QuestionOptionEntity createOptionEntity(com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire.QuestionOption optionDto, QuestionEntity question) {
        var optionEntity = new QuestionOptionEntity();
        optionEntity.setQuestion(question);
        optionEntity.setOptionText(optionDto.getOptionText());
        optionEntity.setOptionValue(optionDto.getOptionValue());
        optionEntity.setOrderIndex(optionDto.getOrderIndex());
        return optionEntity;
    }

    private void saveQuestionResponses(List<com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire.QuestionResponse> responses, QuestionnaireResponseEntity responseEntity) {
        log.info("Saving {} individual question responses", responses.size());

        // Clear existing responses for this questionnaire response (in case of update)
        var existingResponses = questionResponseRepository.findByQuestionnaireResponseId(responseEntity.getId());
        if (!existingResponses.isEmpty()) {
            questionResponseRepository.deleteAll(existingResponses);
            log.info("Deleted {} existing question responses", existingResponses.size());
        }

        // Save new question responses
        for (var responseDto : responses) {
            try {
                // Find the question by its reference ID
                var question = questionRepository.findByReferenceId(responseDto.getQuestionId())
                        .orElseThrow(() -> new ApiException("Question not found with ID: " + responseDto.getQuestionId()));

                // Create new question response entity
                var questionResponse = new QuestionResponseEntity();
                questionResponse.setQuestionnaireResponse(responseEntity);
                questionResponse.setQuestion(question);
                questionResponse.setAnswerValue(responseDto.getAnswerValue());
                questionResponse.setIsSkipped(responseDto.getIsSkipped() != null ? responseDto.getIsSkipped() : false);

                // Save the question response
                questionResponseRepository.save(questionResponse);

                log.debug("Saved response for question {}: {}",
                        responseDto.getQuestionId(),
                        responseDto.getIsSkipped() ? "SKIPPED" : responseDto.getAnswerValue());

            } catch (Exception e) {
                log.error("Failed to save response for question {}: {}", responseDto.getQuestionId(), e.getMessage());
                // Continue with other responses instead of failing the entire operation
            }
        }

        log.info("Successfully saved question responses for questionnaire response: {}", responseEntity.getReferenceId());
    }
}
