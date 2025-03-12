package com.familyFirstSoftware.SecureDocAIBackend.repository;

import com.familyFirstSoftware.SecureDocAIBackend.dto.api.IDocument;
import com.familyFirstSoftware.SecureDocAIBackend.entity.DocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Querys.SELECT_COUNT_DOCUMENTS_QUERY;
import static com.familyFirstSoftware.SecureDocAIBackend.constant.Querys.SELECT_DOCUMENTS_QUERY;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 3/11/2025
 */

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    // We don't just want to document, we want all the user info who owns the document. So we have to join two tables
    @Query(
            countQuery = SELECT_COUNT_DOCUMENTS_QUERY,
            value = SELECT_DOCUMENTS_QUERY,
            nativeQuery = true
    )
    Page<IDocument> findDocuments(Pageable pageable);

}

