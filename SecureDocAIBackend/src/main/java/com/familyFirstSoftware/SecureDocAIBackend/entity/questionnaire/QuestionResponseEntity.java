package com.familyFirstSoftware.SecureDocAIBackend.entity.questionnaire;

import com.familyFirstSoftware.SecureDocAIBackend.entity.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * Entity representing a user's response to an individual question
 */
@Entity
@Table(name = "question_responses")
@Getter
@Setter
public class QuestionResponseEntity extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionnaire_response_id", nullable = false)
    @JsonBackReference
    private QuestionnaireResponseEntity questionnaireResponse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity question;

    @Column(columnDefinition = "text")
    private String answerValue;

    @Column(nullable = false)
    private Boolean isSkipped = false;

    @Column
    private LocalDateTime respondedAt;

    @PrePersist
    protected void onCreate() {
        super.beforePersist();
        if (respondedAt == null && !isSkipped) {
            respondedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        super.beforeUpdate();
        if (!isSkipped && respondedAt == null) {
            respondedAt = LocalDateTime.now();
        }
    }
}
