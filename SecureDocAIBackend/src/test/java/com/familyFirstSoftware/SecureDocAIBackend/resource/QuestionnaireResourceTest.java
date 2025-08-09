package com.familyFirstSoftware.SecureDocAIBackend.resource;

import com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire.Questionnaire;
import com.familyFirstSoftware.SecureDocAIBackend.service.QuestionnaireService;
import com.familyFirstSoftware.SecureDocAIBackend.domain.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuestionnaireResourceTest {

    @Mock
    private QuestionnaireService questionnaireService;

    @InjectMocks
    private QuestionnaireResource questionnaireResource;

    @Test
    @DisplayName("Test get questionnaire by ID - success")
    void getQuestionnaireById_Success() {
        // Arrange
        String questionnaireId = "1";
        Questionnaire mockQuestionnaire = Questionnaire.builder()
                .id(questionnaireId)
                .title("Sample Questionnaire")
                .build();
        when(questionnaireService.getQuestionnaireById(questionnaireId)).thenReturn(mockQuestionnaire);
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Act
        ResponseEntity<Response> responseEntity = questionnaireResource.getQuestionnaireById(questionnaireId, request);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().data()).containsKey("questionnaire");
        assertThat(responseEntity.getBody().data().get("questionnaire")).isEqualTo(mockQuestionnaire);
        assertThat(responseEntity.getBody().message()).isEqualTo("Questionnaire retrieved successfully");
    }

    @Test
    @DisplayName("Test get questionnaire by ID - not found")
    void getQuestionnaireById_NotFound() {
        // Arrange
        String questionnaireId = "2";
        when(questionnaireService.getQuestionnaireById(questionnaireId)).thenThrow(new IllegalStateException("Questionnaire not found"));
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Act & Assert
        assertThatThrownBy(() -> questionnaireResource.getQuestionnaireById(questionnaireId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Questionnaire not found");
    }
}
