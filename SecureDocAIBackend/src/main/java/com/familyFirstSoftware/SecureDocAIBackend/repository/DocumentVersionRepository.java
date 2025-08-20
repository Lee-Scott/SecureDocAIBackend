package com.familyFirstSoftware.SecureDocAIBackend.repository;

import com.familyFirstSoftware.SecureDocAIBackend.entity.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
    Optional<DocumentVersion> findByDocumentIdAndVersionNumber(String documentId, String versionNumber);
    long countByDocumentId(String documentId);
    List<DocumentVersion> findByDocumentIdOrderByCreatedAtDesc(String documentId);
}
