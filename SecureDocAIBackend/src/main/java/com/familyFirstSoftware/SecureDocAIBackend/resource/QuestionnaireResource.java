package com.familyFirstSoftware.SecureDocAIBackend.resource;

import com.familyFirstSoftware.SecureDocAIBackend.domain.Response;
import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire.Questionnaire;
import com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire.QuestionnaireResponse;
import com.familyFirstSoftware.SecureDocAIBackend.service.QuestionnaireService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.familyFirstSoftware.SecureDocAIBackend.utils.RequestUtils.getResponse;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * REST controller for questionnaire operations
 */
@RestController
@RequestMapping("/api/questionnaires")
@RequiredArgsConstructor
@Slf4j
public class QuestionnaireResource {

    private final QuestionnaireService questionnaireService;

    /**
     * GET /api/questionnaires - Get Questionnaires List
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getQuestionnaires(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String title,
            HttpServletRequest request) {

        log.info("Fetching questionnaires - page: {}, size: {}, category: {}, title: {}",
                page, size, category, title);

            Page<Questionnaire> questionnaires = questionnaireService.getQuestionnaires(page, size, category, title);

        Map<String, Object> data = Map.of(
                "questionnaires", questionnaires.getContent(),
                "totalCount", questionnaires.getTotalElements(),
                "currentPage", questionnaires.getNumber(),
                "totalPages", questionnaires.getTotalPages()
        );

        return ResponseEntity.ok(getResponse(request, data, "Questionnaires retrieved successfully", OK));
    }

    /**
     * GET /api/questionnaires/{id} - Get Questionnaire Details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getQuestionnaireById(@PathVariable String id, HttpServletRequest request) {
        log.info("Fetching questionnaire details for ID: {}", id);
        Questionnaire questionnaire = questionnaireService.getQuestionnaireById(id);
        Map<String, Object> data = Map.of("questionnaire", questionnaire);
        return ResponseEntity.ok(getResponse(request, data, "Questionnaire retrieved successfully", OK));
    }

    /**
     * POST /api/questionnaires - Create Questionnaire (Admin)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> createQuestionnaire(@Valid @RequestBody Questionnaire questionnaire, HttpServletRequest request) {
        log.info("Creating new questionnaire: {}", questionnaire.getTitle());

        Questionnaire createdQuestionnaire = questionnaireService.createQuestionnaire(questionnaire);
        URI location = URI.create("/api/questionnaires/" + createdQuestionnaire.getId());
        Map<String, Object> data = Map.of("questionnaire", createdQuestionnaire);

        return ResponseEntity.created(location)
                .body(getResponse(request, data, "Questionnaire created successfully", CREATED));
    }

    /**
     * PATCH /api/questionnaires/{id} - Update Questionnaire (Admin)
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> updateQuestionnaire(@PathVariable String id,
                                                       @RequestBody Questionnaire questionnaire,
                                                       HttpServletRequest request) {
        log.info("Updating questionnaire: {}", id);

        Questionnaire updatedQuestionnaire = questionnaireService.updateQuestionnaire(id, questionnaire);
        Map<String, Object> data = Map.of("questionnaire", updatedQuestionnaire);
        return ResponseEntity.ok(getResponse(request, data, "Questionnaire updated successfully", OK));
    }

    /**
     * DELETE /api/questionnaires/{id} - Delete Questionnaire (Admin)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> deleteQuestionnaire(@PathVariable String id, HttpServletRequest request) {
        log.info("Deleting questionnaire: {}", id);

        questionnaireService.deleteQuestionnaire(id);
        Map<String, Object> data = Map.of("deletedId", id);
        return ResponseEntity.ok(getResponse(request, data, "Questionnaire deleted successfully", OK));
    }

    /**
     * POST /api/questionnaires/responses - Submit Response
     */
    @PostMapping("/responses")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> submitResponse(@Valid @RequestBody QuestionnaireResponse response,
                                                  Authentication authentication,
                                                  HttpServletRequest request) {
        log.info("Submitting questionnaire response for questionnaire: {}", response.getQuestionnaireId());

        User user = (User) authentication.getPrincipal();

        QuestionnaireResponse submittedResponse = questionnaireService.submitResponse(response, user);
        URI location = URI.create("/api/questionnaires/responses/" + submittedResponse.getId());
        Map<String, Object> data = Map.of("response", submittedResponse);

        return ResponseEntity.created(location)
                .body(getResponse(request, data, "Response submitted successfully", CREATED));
    }

    /**
     * GET /api/questionnaires/responses/my - Get User's Responses
     */
    @GetMapping("/responses/my")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getUserResponses(Authentication authentication, HttpServletRequest request) {
        User user = (User) authentication.getPrincipal();
        log.info("Fetching responses for user: {}", user.getUserId());

        List<QuestionnaireResponse> responses = questionnaireService.getUserResponses(user.getUserId());
        Map<String, Object> data = Map.of("responses", responses);
        return ResponseEntity.ok(getResponse(request, data, "User responses retrieved successfully", OK));
    }

    /**
     * GET /api/questionnaires/responses/{id} - Get Specific Response
     */
    @GetMapping("/responses/{id}")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getResponseById(@PathVariable String id, HttpServletRequest request) {
        log.info("Fetching response: {}", id);

        QuestionnaireResponse response = questionnaireService.getResponseById(id);
        Map<String, Object> data = Map.of("response", response);
        return ResponseEntity.ok(getResponse(request, data, "Response retrieved successfully", OK));
    }

    /**
     * PATCH /api/questionnaires/responses/{id} - Update Response
     */
    @PatchMapping("/responses/{id}")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> updateResponse(@PathVariable String id,
                                                  @RequestBody QuestionnaireResponse response,
                                                  HttpServletRequest request) {
        log.info("Updating response: {}", id);

        QuestionnaireResponse updatedResponse = questionnaireService.updateResponse(id, response);
        Map<String, Object> data = Map.of("response", updatedResponse);
        return ResponseEntity.ok(getResponse(request, data, "Response updated successfully", OK));
    }

    /**
     * GET /api/questionnaires/{id}/analytics - Get Analytics (Admin)
     */
    @GetMapping("/{id}/analytics")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getQuestionnaireAnalytics(@PathVariable String id, HttpServletRequest request) {
        log.info("Generating analytics for questionnaire: {}", id);

        Object analytics = questionnaireService.getQuestionnaireAnalytics(id);
        Map<String, Object> data = Map.of("analytics", analytics);
        return ResponseEntity.ok(getResponse(request, data, "Analytics retrieved successfully", OK));
    }

    /**
     * GET /api/questionnaires/active - Get Active Questionnaires
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getActiveQuestionnaires(HttpServletRequest request) {
        log.info("Fetching all active questionnaires");

        List<Questionnaire> questionnaires = questionnaireService.getActiveQuestionnaires();
        Map<String, Object> data = Map.of("questionnaires", questionnaires);
        return ResponseEntity.ok(getResponse(request, data, "Active questionnaires retrieved successfully", OK));
    }
}
