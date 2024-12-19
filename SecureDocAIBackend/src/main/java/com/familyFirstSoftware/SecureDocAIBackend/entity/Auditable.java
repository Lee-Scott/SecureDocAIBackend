package com.familyFirstSoftware.SecureDocAIBackend.entity;


import com.familyFirstSoftware.SecureDocAIBackend.domain.RequestContext;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.AlternativeJdkIdGenerator;

import java.time.LocalDateTime;

/*
    This will never be persisted to the DB, the child classes will.
    Provides a foundation for entities that require auditing information,
    making it easier to track changes and updates to data in the application
    called entity because they are persisted in the DB and JPA calls them entities
 */
@Getter
@Setter
@MappedSuperclass // map this to the DB to child classes
@EntityListeners(AuditingEntityListener.class) // specifies a listener for all the child classes or entities
@JsonIgnoreProperties(value = { "createAt", "updateAt" }, allowGetters = true)
public abstract class Auditable {

    @Id
    @SequenceGenerator(name = "primary_key_seq", sequenceName = "primary_key_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "primary_key_seq")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    private String referenceId = new AlternativeJdkIdGenerator().generateId().toString();

    // TODO : make updatedBy and updatedAt an array so we can keep track for multiple changes? Maybe a whole history field might be easier for the sql keys
    @NotNull
    private Long createdBy; // user who created this entity
    @NotNull
    private Long updatedBy; // user who last updated this entity

    @NotNull
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @CreatedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // called before the entity is persisted in the DB
    @PrePersist
    public void prePersist() {
        var userId = RequestContext.getUserId();

        if(userId == null) {
            throw new ApiException("Cannot persist entity without user ID");

        }

        setCreatedAt(LocalDateTime.now());
        setCreatedBy(userId);
        setUpdatedBy(userId);
        setUpdatedAt(LocalDateTime.now());

    }

    @PreUpdate
    public void beforeUpdate() {
        var userId = RequestContext.getUserId();
        if(userId == null) {
            throw new ApiException("Cannot update entity without user ID");

        }
        setUpdatedAt(LocalDateTime.now());
        setUpdatedBy(userId);
    }




}
