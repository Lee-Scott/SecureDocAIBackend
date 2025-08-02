package com.familyFirstSoftware.SecureDocAIBackend.repository;

import com.familyFirstSoftware.SecureDocAIBackend.entity.questionnaire.QuestionnaireResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.*;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * Repository for questionnaire response operations
 */
@Repository
public interface QuestionnaireResponseRepository extends JpaRepository<QuestionnaireResponseEntity, Long> {

    Optional<QuestionnaireResponseEntity> findByReferenceId(String referenceId);

    List<QuestionnaireResponseEntity> findByUserId(Long userId);

    Page<QuestionnaireResponseEntity> findByUserId(Long userId, Pageable pageable);

    Optional<QuestionnaireResponseEntity> findByQuestionnaireIdAndUserId(Long questionnaireId, Long userId);

    @Query(QUESTIONNAIRE_RESPONSE_FIND_BY_QUESTIONNAIRE_ID_QUERY)
    Page<QuestionnaireResponseEntity> findByQuestionnaireId(@Param("questionnaireId") Long questionnaireId, Pageable pageable);

    @Query(QUESTIONNAIRE_RESPONSE_COUNT_BY_QUESTIONNAIRE_ID_QUERY)
    Long countByQuestionnaireId(@Param("questionnaireId") Long questionnaireId);

    @Query(QUESTIONNAIRE_RESPONSE_COUNT_COMPLETED_BY_QUESTIONNAIRE_ID_QUERY)
    Long countCompletedByQuestionnaireId(@Param("questionnaireId") Long questionnaireId);

    @Query(value = QUESTIONNAIRE_RESPONSE_AVG_COMPLETION_TIME_QUERY, nativeQuery = true)
    Double getAverageCompletionTimeMinutes(@Param("questionnaireId") Long questionnaireId);
}
