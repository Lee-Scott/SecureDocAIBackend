package com.familyFirstSoftware.SecureDocAIBackend.repository;

import com.familyFirstSoftware.SecureDocAIBackend.dto.api.IDocument;
import com.familyFirstSoftware.SecureDocAIBackend.entity.DocumentEntity;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
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
    @Query(countQuery = DOCUMENT_COUNT_ALL_QUERY, value = DOCUMENT_SELECT_ALL_QUERY, nativeQuery = true)
    Page<IDocument> findDocuments(Pageable pageable);

    // @Param("documentName") passed in with: ~* :documentName
    @Query(countQuery = DOCUMENT_COUNT_BY_NAME_QUERY, value = DOCUMENT_SELECT_BY_NAME_QUERY, nativeQuery = true)
    Page<IDocument> findDocumentsByName(@Param("documentName") String documentName, Pageable pageable);

    // specified location with String documentId
    @Query(value = DOCUMENT_SELECT_BY_ID_QUERY, nativeQuery = true)
    Optional<IDocument> findDocumentByDocumentId(String documentId);

    // fetch by referenceId using native projection
    @Query(value = DOCUMENT_SELECT_BY_REFERENCE_QUERY, nativeQuery = true)
    Optional<IDocument> findDocumentByReferenceId(String referenceId);
    
    /**
     * Checks if a document exists with the given documentId
     * @param documentId The ID of the document to check
     * @return true if a document with the given ID exists, false otherwise
     */
    @Query("SELECT COUNT(d) > 0 FROM DocumentEntity d WHERE d.documentId = :documentId")
    boolean existsByDocumentId(@Param("documentId") String documentId);

    Optional<DocumentEntity> findByDocumentId(String documentId);
    Optional<DocumentEntity> findByReferenceId(String referenceId);

    boolean existsByCreatedBy(Long createdBy);
    boolean existsByUpdatedBy(Long updatedBy);

    default void checkUserHasNoDocuments(Long userEntityId) {
        if (existsByCreatedBy(userEntityId) || existsByUpdatedBy(userEntityId)) {
            throw new ApiException("Cannot delete user: User has associated documents. Delete the documents first.");
        }
    }
}
