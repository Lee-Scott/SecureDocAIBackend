package com.familyFirstSoftware.SecureDocAIBackend.service;

import com.familyFirstSoftware.SecureDocAIBackend.config.DocumentConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.familyFirstSoftware.SecureDocAIBackend.entity.DocumentLock;
import com.familyFirstSoftware.SecureDocAIBackend.exception.DocumentOperationException;
import com.familyFirstSoftware.SecureDocAIBackend.repository.DocumentLockRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Service for managing document locks to prevent concurrent modifications.
 * Handles acquiring, releasing, and checking the status of document locks.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentLockService {

    private final DocumentLockRepository lockRepository;
    private final DocumentRepository documentRepository;
    private final DocumentConfigProperties configProperties;

    /**
     * Attempts to acquire a lock on the specified document for the given user.
     *
     * @param documentId the ID of the document to lock
     * @param userId the ID of the user requesting the lock
     * @param lockType the type of lock being acquired
     * @param reason optional reason for the lock
     * @return the acquired lock
     * @throws DocumentOperationException if the lock cannot be acquired
     */
    @Transactional
    public DocumentLock acquireLock(String documentId, String userId, 
                                  DocumentLock.LockType lockType, String reason) {
        // Verify document exists
        if (!documentRepository.existsByDocumentId(documentId)) {
            throw DocumentOperationException.builder()
                    .message("Document not found")
                    .status(HttpStatus.NOT_FOUND)
                    .operation("acquireLock")
                    .documentId(documentId)
                    .build();
        }

        // Clean up any expired locks first
        cleanExpiredLocks();

        // Check for existing lock
        return lockRepository.findByDocumentId(documentId)
                .map(existingLock -> {
                    if (userId.equals(existingLock.getUserId())) {
                        // User already has a lock, extend it
                        return extendLock(existingLock, userId);
                    } else if (existingLock.isActive()) {
                        // Document is locked by another user
                        throw DocumentOperationException.builder()
                                .message(String.format("Document is locked by user %s", existingLock.getUserId()))
                                .status(HttpStatus.CONFLICT)
                                .operation("acquireLock")
                                .documentId(documentId)
                                .build();
                    } else {
                        // Existing lock is expired, replace it
                        return createNewLock(documentId, userId, lockType, reason);
                    }
                })
                .orElseGet(() -> createNewLock(documentId, userId, lockType, reason));
    }

    /**
     * Releases a lock on a document.
     *
     * @param documentId the ID of the document to unlock
     * @param userId the ID of the user releasing the lock
     * @return true if the lock was released, false if no lock existed
     * @throws DocumentOperationException if the user doesn't own the lock
     */
    @Transactional
    public boolean releaseLock(String documentId, String userId) {
        return lockRepository.findByDocumentId(documentId)
                .map(lock -> {
                    if (!userId.equals(lock.getUserId())) {
                        throw DocumentOperationException.builder()
                                .message("You don't have permission to release this lock")
                                .status(HttpStatus.FORBIDDEN)
                                .operation("releaseLock")
                                .documentId(documentId)
                                .build();
                    }
                    lockRepository.delete(lock);
                    log.info("Lock released for document {} by user {}", documentId, userId);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Extends the expiration time of an existing lock.
     *
     * @param lock the lock to extend
     * @param userId the ID of the user extending the lock
     * @return the updated lock
     * @throws DocumentOperationException if the user doesn't own the lock
     */
    @Transactional
    public DocumentLock extendLock(DocumentLock lock, String userId) {
        if (!userId.equals(lock.getUserId())) {
            throw DocumentOperationException.builder()
                    .message("You don't have permission to extend this lock")
                    .status(HttpStatus.FORBIDDEN)
                    .operation("extendLock")
                    .documentId(lock.getDocumentId())
                    .build();
        }

        LocalDateTime newExpiry = LocalDateTime.now().plus(configProperties.getLock().getDefaultTimeout());
        lock.setExpiresAt(newExpiry);
        log.debug("Extended lock for document {} until {}", lock.getDocumentId(), newExpiry);
        return lockRepository.save(lock);
    }

    /**
     * Gets the lock status of a document.
     *
     * @param documentId the ID of the document
     * @return a map containing the lock status information
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getLockStatus(String documentId) {
        return lockRepository.findByDocumentId(documentId)
                .map(lock -> {
                    Map<String, Object> status = new HashMap<>();
                    status.put("isLocked", lock.isActive());
                    status.put("lockedBy", lock.getUserId());
                    status.put("lockedAt", lock.getLockTimestamp());
                    status.put("expiresAt", lock.getExpiresAt());
                    status.put("lockType", lock.getLockType());
                    status.put("lockReason", lock.getLockReason());
                    return status;
                })
                .orElseGet(() -> Map.of("isLocked", false));
    }

    /**
     * Cleans up expired locks.
     * This method is scheduled to run periodically.
     */
    @Scheduled(fixedRateString = "${document.lock.cleanup-interval:300000}")
    @Transactional
    public void cleanExpiredLocks() {
        if (!configProperties.getLock().isAutoCleanupEnabled()) {
            return;
        }

        List<DocumentLock> expiredLocks = lockRepository.findExpiredLocks();
        if (!expiredLocks.isEmpty()) {
            log.info("Cleaning up {} expired locks", expiredLocks.size());
            int deleted = lockRepository.deleteExpiredLocks();
            log.debug("Cleaned up {} expired locks", deleted);
        }
    }

    private DocumentLock createNewLock(String documentId, String userId, 
                                     DocumentLock.LockType lockType, String reason) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plus(configProperties.getLock().getDefaultTimeout());

        DocumentLock lock = new DocumentLock();
        lock.setDocumentId(documentId);
        lock.setUserId(userId);
        lock.setLockType(lockType);
        lock.setLockReason(reason);
        lock.setExpiresAt(expiresAt);

        log.info("Created new {} lock for document {} by user {} until {}", 
                lockType, documentId, userId, expiresAt);
        return lockRepository.save(lock);
    }

    // Additional helper methods...
    
    /**
     * Forcefully releases a lock (admin only).
     *
     * @param documentId the ID of the document
     * @param adminUserId the ID of the admin user
     * @return true if a lock was removed, false otherwise
     */
    @Transactional
    public boolean forceReleaseLock(String documentId, String adminUserId) {
        return lockRepository.findByDocumentId(documentId)
                .map(lock -> {
                    log.warn("Admin {} force-released lock on document {} (was held by user {})",
                            adminUserId, documentId, lock.getUserId());
                    lockRepository.delete(lock);
                    return true;
                })
                .orElse(false);
    }
    
    /**
     * Gets all active locks for a user.
     *
     * @param userId the ID of the user
     * @return list of active locks for the user
     */
    @Transactional(readOnly = true)
    public List<DocumentLock> getUserLocks(String userId) {
        return lockRepository.findActiveLocksByUserId(userId);
    }
}
