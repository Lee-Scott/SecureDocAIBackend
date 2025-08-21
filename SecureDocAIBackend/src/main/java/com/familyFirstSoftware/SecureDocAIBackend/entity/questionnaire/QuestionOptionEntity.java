package com.familyFirstSoftware.SecureDocAIBackend.entity.questionnaire;

import com.familyFirstSoftware.SecureDocAIBackend.entity.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * Entity representing an option for multiple choice questions
 */
@Entity
@Table(name = "question_options")
@Getter
@Setter
public class QuestionOptionEntity extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonBackReference
    private QuestionEntity question;

    @Column(nullable = false)
    private String optionText;

    @Column(nullable = false)
    private String optionValue;

    @Column(nullable = false)
    private Integer orderIndex;
}
