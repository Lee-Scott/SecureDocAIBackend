package com.familyFirstSoftware.SecureDocAIBackend.repository;

import com.familyFirstSoftware.SecureDocAIBackend.entity.questionnaire.QuestionnaireEntity;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.questionnaire.QuestionnaireCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.*;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * Repository for questionnaire operations
 */
@Repository
public interface QuestionnaireRepository extends JpaRepository<QuestionnaireEntity, Long> {

    Optional<QuestionnaireEntity> findByReferenceId(String referenceId);

    Page<QuestionnaireEntity> findByIsActiveTrue(Pageable pageable);

    Page<QuestionnaireEntity> findByCategoryAndIsActiveTrue(QuestionnaireCategory category, Pageable pageable);

    @Query(QUESTIONNAIRE_FIND_WITH_FILTERS_QUERY)
    Page<QuestionnaireEntity> findQuestionnairesWithFilters(
            @Param("category") QuestionnaireCategory category,
            @Param("title") String title,
            Pageable pageable);

    @Query(QUESTIONNAIRE_COUNT_BY_CREATED_BY_QUERY)
    Long countByCreatedBy(@Param("userId") Long userId);
}
