package com.familyFirstSoftware.SecureDocAIBackend.repository;

import com.familyFirstSoftware.SecureDocAIBackend.entity.questionnaire.QuestionResponseEntity;
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
 * Repository for individual question response operations
 */
@Repository
public interface QuestionResponseRepository extends JpaRepository<QuestionResponseEntity, Long> {

    Optional<QuestionResponseEntity> findByReferenceId(String referenceId);

    List<QuestionResponseEntity> findByQuestionnaireResponseId(Long questionnaireResponseId);

    @Query(QUESTION_RESPONSE_FIND_BY_QUESTION_ID_QUERY)
    List<QuestionResponseEntity> findByQuestionId(@Param("questionId") Long questionId);

    @Query(QUESTION_RESPONSE_COUNT_BY_QUESTION_ID_QUERY)
    Long countResponsesByQuestionId(@Param("questionId") Long questionId);

    @Query(QUESTION_RESPONSE_COUNT_SKIPPED_BY_QUESTION_ID_QUERY)
    Long countSkippedByQuestionId(@Param("questionId") Long questionId);

    @Query(QUESTION_RESPONSE_ANSWER_DISTRIBUTION_QUERY)
    List<Object[]> getAnswerDistributionByQuestionId(@Param("questionId") Long questionId);
}
