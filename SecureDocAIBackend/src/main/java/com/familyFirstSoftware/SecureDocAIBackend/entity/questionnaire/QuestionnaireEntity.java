package com.familyFirstSoftware.SecureDocAIBackend.entity.questionnaire;

import com.familyFirstSoftware.SecureDocAIBackend.entity.Auditable;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.questionnaire.QuestionnaireCategory;
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
 * Entity representing a questionnaire
 */
@Entity
@Table(name = "questionnaires")
@Getter
@Setter
public class QuestionnaireEntity extends Auditable {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionnaireCategory category;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Integer estimatedTimeMinutes;

    @OneToMany(mappedBy = "questionnaire", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("pageNumber ASC")
    @JsonManagedReference
    private List<QuestionPageEntity> pages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id")
    private UserEntity creator;

    public Integer getTotalQuestions() {
        if (pages == null) return 0;
        return pages.stream()
                .mapToInt(page -> page.getQuestions() != null ? page.getQuestions().size() : 0)
                .sum();
    }
}
