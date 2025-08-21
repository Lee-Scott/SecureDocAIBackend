package com.familyFirstSoftware.SecureDocAIBackend.service;

import com.familyFirstSoftware.SecureDocAIBackend.dto.questionnaire.Questionnaire;
import com.familyFirstSoftware.SecureDocAIBackend.entity.questionnaire.QuestionnaireEntity;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.familyFirstSoftware.SecureDocAIBackend.repository.QuestionRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.QuestionnaireRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.QuestionnaireResponseRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.UserRepository;
import com.familyFirstSoftware.SecureDocAIBackend.service.impl.QuestionnaireServiceImpl;
import com.familyFirstSoftware.SecureDocAIBackend.utils.QuestionnaireDtoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for QuestionnaireServiceImpl.
 *
 * Naming Convention:
 * methodName_Scenario_ExpectedBehavior
 * e.g., getQuestionnaireById_WhenExists_ReturnsDto
 */
@ExtendWith(MockitoExtension.class)
class QuestionnaireServiceTest {

    // --- Mocks and Service Under Test ---

    @Mock
    private QuestionnaireRepository questionnaireRepository;

    @Mock
    private QuestionnaireResponseRepository responseRepository;

    // Note: The original service had a duplicate 'QuestionResponseRepository'.
    // We mock it once here as it's the same type.
    @Mock
    private com.familyFirstSoftware.SecureDocAIBackend.repository.QuestionResponseRepository questionResponseRepository;


    @Mock
    private UserRepository userRepository;

    @Mock
    private QuestionnaireDtoMapper dtoMapper;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionnaireServiceImpl questionnaireService;


    // --- Test Cases ---

    @Test
    @DisplayName("Get Questionnaire By ID - Happy Path")
    void getQuestionnaireById_WhenExists_ReturnsDto() {
        // Arrange
        String questionnaireId = "q-uuid-123";
        QuestionnaireEntity mockEntity = TestData.buildQuestionnaireEntity(questionnaireId, "Health Check");
        Questionnaire mockDto = TestData.buildQuestionnaireDto(questionnaireId, "Health Check");

        when(questionnaireRepository.findByReferenceId(questionnaireId)).thenReturn(Optional.of(mockEntity));
        when(dtoMapper.toDto(mockEntity)).thenReturn(mockDto);

        // Act
        Questionnaire result = questionnaireService.getQuestionnaireById(questionnaireId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(questionnaireId);
        assertThat(result.getTitle()).isEqualTo("Health Check");

        // Verify interactions
        verify(questionnaireRepository).findByReferenceId(questionnaireId);
        verify(dtoMapper).toDto(mockEntity);
        verifyNoMoreInteractions(questionnaireRepository, dtoMapper); // Ensures no other methods were called
    }

    @Test
    @DisplayName("Get Questionnaire By ID - Not Found")
    void getQuestionnaireById_WhenNotExists_ThrowsApiException() {
        // Arrange
        String questionnaireId = "q-uuid-404";
        when(questionnaireRepository.findByReferenceId(questionnaireId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> questionnaireService.getQuestionnaireById(questionnaireId))
                .isInstanceOf(ApiException.class)
                .hasMessage("Questionnaire not found with ID: " + questionnaireId);

        // Verify that no mapping occurred
        verify(dtoMapper, never()).toDto(any(QuestionnaireEntity.class));
    }

    @Test
    @DisplayName("Create Questionnaire - Valid Input")
    void createQuestionnaire_WithValidData_ReturnsSavedDto() {
        // Arrange
        Questionnaire inputDto = TestData.buildQuestionnaireDto(null, "New Compliance Survey");
        QuestionnaireEntity entityToSave = new QuestionnaireEntity(); // Mapper would create this
        QuestionnaireEntity savedEntity = TestData.buildQuestionnaireEntity("q-uuid-new", "New Compliance Survey");
        Questionnaire resultDto = TestData.buildQuestionnaireDto("q-uuid-new", "New Compliance Survey");

        // We don't need to mock the mapper from DTO to Entity, as the service does it internally.
        // We mock the repository save and the final mapping back to DTO.
        when(questionnaireRepository.save(any(QuestionnaireEntity.class))).thenReturn(savedEntity);
        when(dtoMapper.toDto(savedEntity)).thenReturn(resultDto);

        // Act
        Questionnaire result = questionnaireService.createQuestionnaire(inputDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("q-uuid-new");
        assertThat(result.getTitle()).isEqualTo(inputDto.getTitle());

        // --- ArgumentCaptor Example ---
        // Use ArgumentCaptor to inspect the exact object that was passed to a method.
        // This is useful for verifying complex objects or specific fields without
        // needing to override equals/hashCode in production code just for tests.
        ArgumentCaptor<QuestionnaireEntity> entityCaptor = ArgumentCaptor.forClass(QuestionnaireEntity.class);
        verify(questionnaireRepository).save(entityCaptor.capture());
        QuestionnaireEntity capturedEntity = entityCaptor.getValue();

        assertThat(capturedEntity.getTitle()).isEqualTo("New Compliance Survey");
        assertThat(capturedEntity.getIsActive()).isTrue(); // Verify default value logic
    }

    @Test
    @DisplayName("Delete Questionnaire - When Exists")
    void deleteQuestionnaire_WhenExists_CompletesSuccessfully() {
        // Arrange
        String questionnaireId = "q-uuid-to-delete";
        QuestionnaireEntity mockEntity = TestData.buildQuestionnaireEntity(questionnaireId, "Old Survey");
        when(questionnaireRepository.findByReferenceId(questionnaireId)).thenReturn(Optional.of(mockEntity));
        doNothing().when(questionnaireRepository).delete(mockEntity); // Use doNothing for void methods

        // Act
        questionnaireService.deleteQuestionnaire(questionnaireId);

        // Assert
        // No exception thrown is the success criteria.

        // Verify that the delete method was called with the correct entity.
        verify(questionnaireRepository).findByReferenceId(questionnaireId);
        verify(questionnaireRepository).delete(mockEntity);
        verifyNoMoreInteractions(questionnaireRepository);
    }

    @Test
    @DisplayName("Delete Questionnaire - When Not Exists")
    void deleteQuestionnaire_WhenNotExists_ThrowsApiException() {
        // Arrange
        String questionnaireId = "q-uuid-404";
        when(questionnaireRepository.findByReferenceId(questionnaireId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> questionnaireService.deleteQuestionnaire(questionnaireId))
                .isInstanceOf(ApiException.class)
                .hasMessage("Questionnaire not found with ID: " + questionnaireId);

        // Verify that delete was never called
        verify(questionnaireRepository, never()).delete(any(QuestionnaireEntity.class));
    }


    // --- Test Data Builder ---
    // Using an inner static class for test data builders keeps tests clean and readable.
    // It encapsulates the creation of complex objects needed for tests.
    static class TestData {
                static Questionnaire buildQuestionnaireDto(String id, String title) {
            return Questionnaire.builder()
                    .id(id)
                    .title(title)
                    .description("A sample description.")
                    .category("OTHER")
                    .isActive(true)
                    .estimatedTimeMinutes(15)
                    .build();
        }

        static QuestionnaireEntity buildQuestionnaireEntity(String referenceId, String title) {
            QuestionnaireEntity entity = new QuestionnaireEntity();
            // In a real scenario, the database would set the numeric ID.
            // We use a random long to simulate this.
            entity.setId((long) (Math.random() * 1000));
            entity.setReferenceId(referenceId != null ? referenceId : "q-" + UUID.randomUUID().toString());
            entity.setTitle(title);
            entity.setDescription("A sample description.");
            entity.setIsActive(true);
            return entity;
        }
    }
}
