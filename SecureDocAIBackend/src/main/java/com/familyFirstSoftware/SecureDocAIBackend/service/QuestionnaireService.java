package com.familyFirstSoftware.SecureDocAIBackend.service;

import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire.Questionnaire;
import com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire.QuestionnaireResponse;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.questionnaire.QuestionnaireCategory;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * Service interface for questionnaire operations
 */
public interface QuestionnaireService {

    // Questionnaire CRUD operations
    Questionnaire createQuestionnaire(Questionnaire questionnaire);
    Questionnaire updateQuestionnaire(String questionnaireId, Questionnaire questionnaire);
    void deleteQuestionnaire(String questionnaireId);

    // Questionnaire retrieval
    Questionnaire getQuestionnaireById(String questionnaireId);
    Page<Questionnaire> getQuestionnaires(int page, int size, String category, String title);
    List<Questionnaire> getActiveQuestionnaires();

    // Response operations
    QuestionnaireResponse submitResponse(QuestionnaireResponse response, User user);
    QuestionnaireResponse updateResponse(String responseId, QuestionnaireResponse response);
    QuestionnaireResponse getResponseById(String responseId);
    List<QuestionnaireResponse> getUserResponses(String userId);

    // Analytics
    Object getQuestionnaireAnalytics(String questionnaireId);
}
