package com.familyFirstSoftware.SecureDocAIBackend.entity.questionnaire;

import com.familyFirstSoftware.SecureDocAIBackend.entity.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * Entity representing a page within a questionnaire
 */
@Entity
@Table(name = "question_pages")
@Getter
@Setter
public class QuestionPageEntity extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionnaire_id", nullable = false)
    @JsonBackReference
    private QuestionnaireEntity questionnaire;

    @Column(nullable = false)
    private Integer pageNumber;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean isRequired = true;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("questionNumber ASC")
    @JsonManagedReference
    private List<QuestionEntity> questions;
}
