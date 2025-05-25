package com.familyFirstSoftware.SecureDocAIBackend.repository;

import com.familyFirstSoftware.SecureDocAIBackend.dto.api.IDocument;
import com.familyFirstSoftware.SecureDocAIBackend.entity.DocumentEntity;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.*;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 2/14/2025
 */


@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    @Query(countQuery = SELECT_COUNT_DOCUMENTS_QUERY, value = SELECT_DOCUMENTS_QUERY, nativeQuery = true)
    Page<IDocument> findDocuments(Pageable pageable);

    // @Param("documentName") passed in with: ~* :documentName
    @Query(countQuery = SELECT_COUNT_DOCUMENTS_BY_NAME_QUERY, value = SELECT_DOCUMENTS_BY_NAME_QUERY, nativeQuery = true)
    Page<IDocument> findDocumentsByName(@Param("documentName") String documentName, Pageable pageable);

    // specified location with String documentId
    @Query(value = SELECT_DOCUMENT_QUERY, nativeQuery = true)
    Optional<IDocument> findDocumentByDocumentId(String documentId);

    Optional<DocumentEntity> findByDocumentId(String documentId);
}


