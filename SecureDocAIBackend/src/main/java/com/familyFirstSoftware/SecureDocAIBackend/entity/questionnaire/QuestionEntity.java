package com.familyFirstSoftware.SecureDocAIBackend.entity.questionnaire;

import com.familyFirstSoftware.SecureDocAIBackend.entity.Auditable;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.questionnaire.QuestionType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * Entity representing a question within a questionnaire page
 */
@Entity
@Table(name = "questions")
@Getter
@Setter
public class QuestionEntity extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    @JsonBackReference
    private QuestionPageEntity page;

    @Column(nullable = false)
    private Integer questionNumber;

    @Column(nullable = false)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType;

    @Column(nullable = false)
    private Boolean isRequired = true;

    @Column
    private String helpText;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_validation_rules", joinColumns = @JoinColumn(name = "question_id"))
    @MapKeyColumn(name = "rule_key")
    @Column(name = "rule_value")
    private Map<String, String> validationRules;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    @JsonManagedReference
    private List<QuestionOptionEntity> options;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuestionResponseEntity> questionResponses;
}
