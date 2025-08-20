package com.familyFirstSoftware.SecureDocAIBackend.repository;

import com.familyFirstSoftware.SecureDocAIBackend.entity.DocumentLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing document locks to prevent concurrent modifications.
 * Provides methods to query and manage document locks with various criteria.
 */
public interface DocumentLockRepository extends JpaRepository<DocumentLock, String> {
    
    /**
     * Find a document lock by document ID.
     * @param documentId the ID of the document
     * @return an Optional containing the lock if found, empty otherwise
     */
    Optional<DocumentLock> findByDocumentId(String documentId);
    
    /**
     * Find all active (non-expired) locks for a specific user.
     * @param userId the ID of the user
     * @return list of active locks for the user
     */
    @Query("SELECT l FROM DocumentLock l WHERE l.userId = :userId AND l.expiresAt > CURRENT_TIMESTAMP")
    List<DocumentLock> findActiveLocksByUserId(@Param("userId") String userId);
    
    /**
     * Find all expired locks.
     * @return list of expired locks
     */
    @Query("SELECT l FROM DocumentLock l WHERE l.expiresAt <= CURRENT_TIMESTAMP")
    List<DocumentLock> findExpiredLocks();
    
    /**
     * Delete all expired locks.
     * @return number of locks deleted
     */
    @Modifying
    @Query("DELETE FROM DocumentLock l WHERE l.expiresAt <= CURRENT_TIMESTAMP")
    int deleteExpiredLocks();
    
    /**
     * Extend the expiration time of a lock.
     * @param documentId the ID of the document
     * @param newExpiry the new expiration time
     * @return number of locks updated (0 or 1)
     */
    @Modifying
    @Query("UPDATE DocumentLock l SET l.expiresAt = :newExpiry, l.updatedAt = CURRENT_TIMESTAMP WHERE l.documentId = :documentId")
    int updateLockExpiry(@Param("documentId") String documentId, @Param("newExpiry") LocalDateTime newExpiry);
    
    /**
     * Check if a document is currently locked (has an active lock).
     * @param documentId the ID of the document
     * @return true if the document is locked, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM DocumentLock l WHERE l.documentId = :documentId AND l.expiresAt > CURRENT_TIMESTAMP")
    boolean isDocumentLocked(@Param("documentId") String documentId);
    
    /**
     * Find all locks of a specific type.
     * @param lockType the type of lock to find
     * @return list of matching locks
     */
    List<DocumentLock> findByLockType(DocumentLock.LockType lockType);
}
