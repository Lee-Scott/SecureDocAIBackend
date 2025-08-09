package com.familyFirstSoftware.SecureDocAIBackend.repository;

import com.familyFirstSoftware.SecureDocAIBackend.entity.questionnaire.QuestionnaireEntity;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.questionnaire.QuestionnaireCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Repository test for QuestionnaireRepository using @DataJpaTest.
 *
 * --- Best Practices ---
 * 1.  @DataJpaTest: This annotation provides a pre-configured test slice that includes an
 *     in-memory database (H2 by default), JPA, and Spring Data repositories. It's fast
 *     because it doesn't load the whole application context.
 * 2.  TestEntityManager: A utility class provided by Spring Boot for JPA tests. It's useful
 *     for setting up data in a controlled way before a test runs. Use `persistAndFlush` to
 *     ensure data is written to the database and constraints are checked.
 * 3.  Transactionality: Each @DataJpaTest is transactional and rolls back at the end. This
 *     ensures tests are isolated and don't interfere with each other.
 */
@DataJpaTest
@DisplayName("QuestionnaireRepository Tests")
class QuestionnaireRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    private QuestionnaireEntity healthSurvey;
    private QuestionnaireEntity complianceSurvey;

    @BeforeEach
    void setUp() {
        // Arrange: Create and persist test data before each test
        healthSurvey = createQuestionnaire("Health Survey", QuestionnaireCategory.HEALTHCARE, true);
        complianceSurvey = createQuestionnaire("Compliance Check", QuestionnaireCategory.BUSINESS, true);
        createQuestionnaire("Archived Survey", QuestionnaireCategory.OTHER, false); // Inactive
    }

    @Test
    @DisplayName("findByReferenceId returns entity when exists")
    void findByReferenceId_WhenExists_ShouldReturnEntity() {
        // Act
        var found = questionnaireRepository.findByReferenceId(healthSurvey.getReferenceId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Health Survey");
    }

    @Test
    @DisplayName("findByReferenceId returns empty when not exists")
    void findByReferenceId_WhenNotExists_ShouldReturnEmpty() {
        // Act
        var found = questionnaireRepository.findByReferenceId("non-existent-uuid");

        // Assert
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("findQuestionnairesWithFilters respects category and pagination")
    void findQuestionnairesWithFilters_WithCategory_ShouldReturnFilteredAndPagedResults() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 5, Sort.by("title"));

        // Act
        Page<QuestionnaireEntity> resultPage = questionnaireRepository.findQuestionnairesWithFilters(
                QuestionnaireCategory.HEALTHCARE, null, pageable);

        // Assert
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(1);
        assertThat(resultPage.getContent().get(0).getTitle()).isEqualTo("Health Survey");
    }

    @Test
    @DisplayName("findQuestionnairesWithFilters returns all active when filters are null")
    void findQuestionnairesWithFilters_WithNullFilters_ShouldReturnAllActive() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 5, Sort.by("title"));

        // Act
        Page<QuestionnaireEntity> resultPage = questionnaireRepository.findQuestionnairesWithFilters(null, null, pageable);

        // Assert
        assertThat(resultPage.getTotalElements()).isEqualTo(2); // Health and Compliance
        assertThat(resultPage.getContent()).extracting(QuestionnaireEntity::getTitle)
                .containsExactly("Compliance Check", "Health Survey");
    }

    @Test
    @DisplayName("Unique constraint on referenceId throws exception")
    void save_WithDuplicateReferenceId_ShouldThrowException() {
        // Arrange
        String duplicateId = healthSurvey.getReferenceId();
        QuestionnaireEntity duplicateEntity = new QuestionnaireEntity();
        duplicateEntity.setReferenceId(duplicateId);
        duplicateEntity.setTitle("Another Survey");
        duplicateEntity.setCategory(QuestionnaireCategory.OTHER);

        // Act & Assert
        // Persist the new entity and flush to trigger the database constraint check
        assertThatThrownBy(() -> {
            questionnaireRepository.saveAndFlush(duplicateEntity);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("findQuestionnairesWithFilters with title returns filtered results")
    void findQuestionnairesWithFilters_WithTitle_ShouldReturnFilteredResults() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 5, Sort.by("title"));

        // Act: Search with a partial, case-insensitive title
        Page<QuestionnaireEntity> resultPage = questionnaireRepository.findQuestionnairesWithFilters(
                null, "health", pageable);

        // Assert
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(1);
        assertThat(resultPage.getContent().get(0).getTitle()).isEqualTo("Health Survey");
    }

    @Test
    @DisplayName("findQuestionnairesWithFilters with category and title returns filtered results")
    void findQuestionnairesWithFilters_WithCategoryAndTitle_ShouldReturnFilteredResults() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 5, Sort.by("title"));

        // Act: Search with both category and a partial title
        Page<QuestionnaireEntity> resultPage = questionnaireRepository.findQuestionnairesWithFilters(
                QuestionnaireCategory.HEALTHCARE, "survey", pageable);

        // Assert
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(1);
        assertThat(resultPage.getContent().get(0).getTitle()).isEqualTo("Health Survey");

        // Act: Search with a mismatched category and title
        Page<QuestionnaireEntity> emptyResultPage = questionnaireRepository.findQuestionnairesWithFilters(
                QuestionnaireCategory.BUSINESS, "Health", pageable);

        // Assert
        assertThat(emptyResultPage).isNotNull();
        assertThat(emptyResultPage.getTotalElements()).isZero();
    }

    // Helper method to create and persist a questionnaire entity
    private QuestionnaireEntity createQuestionnaire(String title, QuestionnaireCategory category, boolean isActive) {
        QuestionnaireEntity entity = new QuestionnaireEntity();
        entity.setReferenceId("q-" + UUID.randomUUID());
        entity.setTitle(title);
        entity.setCategory(category);
        entity.setIsActive(isActive);
        return entityManager.persistAndFlush(entity);
    }
}
