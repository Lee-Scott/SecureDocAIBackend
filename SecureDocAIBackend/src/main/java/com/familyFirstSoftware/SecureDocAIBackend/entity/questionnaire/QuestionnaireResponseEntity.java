package com.familyFirstSoftware.SecureDocAIBackend.entity.questionnaire;

import com.familyFirstSoftware.SecureDocAIBackend.entity.Auditable;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * Entity representing a user's response to a questionnaire
 */
@Entity
@Table(name = "questionnaire_responses")
@Getter
@Setter
public class QuestionnaireResponseEntity extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionnaire_id", nullable = false)
    private QuestionnaireEntity questionnaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private Boolean isCompleted = false;

    @Column
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime completedAt;

    @Column
    private LocalDateTime lastModifiedAt;

    @Column
    private Double totalScore;

    @OneToMany(mappedBy = "questionnaireResponse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<QuestionResponseEntity> responses;

    @PrePersist
    protected void onCreate() {
        super.beforePersist();
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
        lastModifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        super.beforeUpdate();
        lastModifiedAt = LocalDateTime.now();
        if (isCompleted && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }
}
