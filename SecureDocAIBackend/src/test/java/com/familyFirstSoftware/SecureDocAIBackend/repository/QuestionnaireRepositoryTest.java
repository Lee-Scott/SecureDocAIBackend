package com.familyFirstSoftware.SecureDocAIBackend.repository;

import com.familyFirstSoftware.SecureDocAIBackend.entity.questionnaire.QuestionnaireEntity;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.questionnaire.QuestionnaireCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class QuestionnaireRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Test
    public void whenFindQuestionnairesWithFilters_withTitle_thenReturnQuestionnaire() {
        // given
        QuestionnaireEntity questionnaire = new QuestionnaireEntity();
        questionnaire.setTitle("Health Assessment");
        questionnaire.setTitleSearch("health assessment");
        questionnaire.setCategory(QuestionnaireCategory.HEALTHCARE);
        questionnaire.setIsActive(true);
        questionnaire.setEstimatedTimeMinutes(15);
        entityManager.persist(questionnaire);
        entityManager.flush();

        // when
        Page<QuestionnaireEntity> found = questionnaireRepository.findQuestionnairesWithFilters(
                null,
                "%health assessment%",
                PageRequest.of(0, 10)
        );

        // then
        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getContent().get(0).getTitle()).isEqualTo(questionnaire.getTitle());
    }

    @Test
    public void whenFindQuestionnairesWithFilters_withCategory_thenReturnQuestionnaire() {
        // given
        QuestionnaireEntity questionnaire = new QuestionnaireEntity();
        questionnaire.setTitle("Health Assessment");
        questionnaire.setTitleSearch("health assessment");
        questionnaire.setCategory(QuestionnaireCategory.HEALTHCARE);
        questionnaire.setIsActive(true);
        questionnaire.setEstimatedTimeMinutes(15);
        entityManager.persist(questionnaire);
        entityManager.flush();

        // when
        Page<QuestionnaireEntity> found = questionnaireRepository.findQuestionnairesWithFilters(
                QuestionnaireCategory.HEALTHCARE,
                null,
                PageRequest.of(0, 10)
        );

        // then
        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getContent().get(0).getCategory()).isEqualTo(QuestionnaireCategory.HEALTHCARE);
    }

    @Test
    public void whenFindQuestionnairesWithFilters_withCategoryAndTitle_thenReturnQuestionnaire() {
        // given
        QuestionnaireEntity questionnaire = new QuestionnaireEntity();
        questionnaire.setTitle("Health Assessment");
        questionnaire.setTitleSearch("health assessment");
        questionnaire.setCategory(QuestionnaireCategory.HEALTHCARE);
        questionnaire.setIsActive(true);
        questionnaire.setEstimatedTimeMinutes(15);
        entityManager.persist(questionnaire);
        entityManager.flush();

        // when
        Page<QuestionnaireEntity> found = questionnaireRepository.findQuestionnairesWithFilters(
                QuestionnaireCategory.HEALTHCARE,
                "%health assessment%",
                PageRequest.of(0, 10)
        );

        // then
        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getContent().get(0).getTitle()).isEqualTo(questionnaire.getTitle());
    }

    @Test
    public void whenFindQuestionnairesWithFilters_withNonMatchingTitle_thenReturnEmpty() {
        // given
        QuestionnaireEntity questionnaire = new QuestionnaireEntity();
        questionnaire.setTitle("Health Assessment");
        questionnaire.setTitleSearch("health assessment");
        questionnaire.setCategory(QuestionnaireCategory.HEALTHCARE);
        questionnaire.setIsActive(true);
        questionnaire.setEstimatedTimeMinutes(15);
        entityManager.persist(questionnaire);
        entityManager.flush();

        // when
        Page<QuestionnaireEntity> found = questionnaireRepository.findQuestionnairesWithFilters(
                null,
                "%finance%",
                PageRequest.of(0, 10)
        );

        // then
        assertThat(found.getContent()).isEmpty();
    }
}
