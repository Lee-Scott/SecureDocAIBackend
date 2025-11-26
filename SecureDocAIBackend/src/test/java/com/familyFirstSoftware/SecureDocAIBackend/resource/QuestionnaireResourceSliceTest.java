package com.familyFirstSoftware.SecureDocAIBackend.resource;

import com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire.Questionnaire;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.familyFirstSoftware.SecureDocAIBackend.service.QuestionnaireService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Slice Test for QuestionnaireResource.
 *
 * --- Tips for Effective Slice Tests ---
 * 1.  Focus on the Web Layer: @WebMvcTest only loads the components needed for the web layer
 *     (controllers, filters, etc.), not the full application context. This makes tests fast.
 * 2.  Mock Services: Use @MockBean to provide mock implementations of services. This isolates
 *     the controller, ensuring you are only testing its logic (request mapping, validation, serialization).
 * 3.  Test Controller Logic, Not Service Logic: Verify that the controller calls the service correctly.
 *     The actual business logic is tested in the service's own unit test.
 * 4.  Security: Use `spring-security-test` annotations like @WithMockUser to simulate authenticated
 *     users and test authorization rules without a full security setup.
 */
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(QuestionnaireResource.class)
@DisplayName("QuestionnaireResource Slice Tests")
@ActiveProfiles("test")
class QuestionnaireResourceSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuestionnaireService questionnaireService;

    private static final String BASE_URL = "/api/questionnaires";

    @Test
    @DisplayName("GET /api/questionnaires/{id} - Success")
    @WithMockUser(roles = {"USER"}) // Simulates a user with ROLE_USER
    void getQuestionnaireById_WhenExists_Returns200() throws Exception {
        // Arrange
        String questionnaireId = "q-123";
        Questionnaire mockQuestionnaire = Questionnaire.builder()
                .id(questionnaireId)
                .title("Health Survey")
                .build();
        given(questionnaireService.getQuestionnaireById(questionnaireId)).willReturn(mockQuestionnaire);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/{id}", questionnaireId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.questionnaire.id", is(questionnaireId)))
                .andExpect(jsonPath("$.data.questionnaire.title", is("Health Survey")))
                .andExpect(jsonPath("$.message", is("Questionnaire retrieved successfully")));
    }

    @Test
    @DisplayName("GET /api/questionnaires/{id} - Not Found")
    @WithMockUser(roles = {"USER"})
    void getQuestionnaireById_WhenNotExists_Returns404() throws Exception {
        // Arrange
        String questionnaireId = "q-404";
        // BDD-style mocking with BDDMockito
        given(questionnaireService.getQuestionnaireById(questionnaireId))
                .willThrow(new ApiException("Questionnaire not found with ID: " + questionnaireId));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/{id}", questionnaireId))
                .andExpect(status().isNotFound()); // Assuming a @ControllerAdvice handles ApiException to return 404
    }

    @Test
    @DisplayName("POST /api/questionnaires - Created")
    @WithMockUser(roles = {"DOCTOR"}) // Endpoint requires DOCTOR or SUPER_ADMIN role
    void createQuestionnaire_WithValidData_Returns201() throws Exception {
        // Arrange
        Questionnaire requestDto = Questionnaire.builder()
                .title("New Compliance Form")
                .build();
        // Set other required fields for validation if any

        Questionnaire responseDto = Questionnaire.builder()
                .id("q-new-456")
                .title("New Compliance Form")
                .build();

        given(questionnaireService.createQuestionnaire(any(Questionnaire.class))).willReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/questionnaires/q-new-456"))
                .andExpect(jsonPath("$.data.questionnaire.id", is("q-new-456")))
                .andExpect(jsonPath("$.message", is("Questionnaire created successfully")));
    }

    @Test
    @DisplayName("POST /api/questionnaires - Validation Failure")
    @WithMockUser(roles = {"DOCTOR"})
    void createQuestionnaire_WithInvalidData_Returns400() throws Exception {
        // Arrange
        // Create a DTO that would fail @Valid validation (e.g., null title if annotated with @NotBlank)
        Questionnaire invalidRequestDto = Questionnaire.builder()
                .title(null)
                .build(); // Assuming title is @NotBlank

        // Act & Assert
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest());

        // Verify that the service was never called because validation failed at the controller level
        verify(questionnaireService, never()).createQuestionnaire(any(Questionnaire.class));
    }

    @Test
    @DisplayName("DELETE /api/questionnaires/{id} - No Content")
    @WithMockUser(roles = {"SUPER_ADMIN"})
    void deleteQuestionnaire_WhenExists_Returns200() throws Exception {
        // Arrange
        String questionnaireId = "q-to-delete";
        // No need to mock the return value since the service method is void.
        // Mockito will do nothing by default.

        // Act & Assert
        mockMvc.perform(delete(BASE_URL + "/{id}", questionnaireId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deletedId", is(questionnaireId)))
                .andExpect(jsonPath("$.message", is("Questionnaire deleted successfully")));

        // Verify the service method was called exactly once with the correct ID
        verify(questionnaireService).deleteQuestionnaire(eq(questionnaireId));
    }

    @Test
    @DisplayName("GET /api/questionnaires - Unauthorized")
    void getQuestionnaires_WhenUnauthenticated_Returns401() throws Exception {
        // Act & Assert
        // Note: We don't use @WithMockUser here to simulate an anonymous user
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isUnauthorized());
    }
}
