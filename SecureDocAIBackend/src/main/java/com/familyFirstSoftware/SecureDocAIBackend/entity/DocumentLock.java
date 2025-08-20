package com.familyFirstSoftware.SecureDocAIBackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Represents a lock on a document to prevent concurrent modifications.
 * Locks expire automatically after a configurable duration.
 */
@Entity
@Table(name = "document_locks",
       indexes = {
           @Index(name = "idx_document_lock_document_id", columnList = "documentId"),
           @Index(name = "idx_document_lock_user_id", columnList = "userId"),
           @Index(name = "idx_document_lock_expires", columnList = "expiresAt")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DocumentLock {
    
    @Id
    @NotBlank(message = "Document ID is required")
    @Column(nullable = false, updatable = false)
    private String documentId;
    
    @NotBlank(message = "User ID is required")
    @Column(nullable = false, updatable = false)
    private String userId;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime lockTimestamp;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @NotNull(message = "Expiration time is required")
    @Future(message = "Expiration time must be in the future")
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(length = 1000)
    private String lockReason;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LockType lockType = LockType.EDIT;
    
    @Version
    private Long version;
    
    /**
     * Checks if the lock is currently active (not expired)
     * @return true if the lock is still valid, false otherwise
     */
    public boolean isActive() {
        return expiresAt != null && expiresAt.isAfter(LocalDateTime.now());
    }
    
    /**
     * Extends the lock duration by the specified amount of time
     * @param duration the duration to extend the lock by
     * @return true if the lock was extended, false if the lock was already expired
     */
    public boolean extendLock(java.time.Duration duration) {
        if (!isActive()) {
            return false;
        }
        this.expiresAt = LocalDateTime.now().plus(duration);
        return true;
    }
    
    public enum LockType {
        EDIT,         // For editing the document
        APPROVAL,     // For document approval workflow
        TRANSLATION,  // For translation in progress
        REVIEW,       // For document review
        SYSTEM        // System-initiated lock
    }
}
