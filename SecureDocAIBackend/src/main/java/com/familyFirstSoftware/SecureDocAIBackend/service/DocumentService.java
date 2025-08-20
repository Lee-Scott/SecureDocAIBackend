package com.familyFirstSoftware.SecureDocAIBackend.service;

import com.familyFirstSoftware.SecureDocAIBackend.dto.Document;
import com.familyFirstSoftware.SecureDocAIBackend.dto.DocumentVersionDto;
import com.familyFirstSoftware.SecureDocAIBackend.dto.api.IDocument;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 3/19/2025
 */

public interface DocumentService {
    Page<IDocument> getDocuments(int page, int size);
    Page<IDocument> getDocuments(int page, int size, String name);
    Collection<Document> saveDocuments(String userId, List<MultipartFile> documents);
    Document reattachDocument(String documentId, MultipartFile file);
    IDocument updateDocument(String documentId, String name, String description);
    void deleteDocument(String documentId);
    IDocument getDocumentByDocumentId(String documentId);
        /**
     * Gets a document resource, optionally a specific version
     * @param documentName The name of the document
     * @param version The version to retrieve (optional)
     * @return The resource representing the document
     * @throws java.io.IOException if the resource cannot be read
     */
    Resource getResource(String documentName, String version) throws java.io.IOException;

    Object checkoutDocument(String documentId, String userId);
    
    /**
     * Refreshes the document lock for the specified document and user.
     * @param documentId The ID of the document to refresh the lock for
     * @param userId The ID of the user who owns the lock
     * @return A map containing lock information if successful
     * @throws ApiException if the document is not found, lock is expired, or lock is owned by another user
     */
    Map<String, Object> refreshDocumentLock(String documentId, String userId);
    
    /**
     * Releases the document lock if it's held by the specified user.
     * @param documentId The ID of the document to check in
     * @param userId The ID of the user attempting to check in
     * @return A map containing the result of the check-in operation
     * @throws ApiException if the document is not found or other errors occur
     */
    Map<String, Object> checkinDocument(String documentId, String userId);
    
    /**
     * Retrieves the current status of a document including lock information.
     * @param documentId The ID of the document to check
     * @return A map containing the document status and lock information
     */
    Map<String, Object> getDocumentStatus(String documentId);
    
    /**
     * Retrieves the version history of a document.
     * @param documentId The ID of the document
     * @return A list of document versions
     */
    List<DocumentVersionDto> getDocumentVersions(String documentId);
    
    /**
     * Reviews one or more documents using AI and returns analysis results
     * @param userId The ID of the user requesting the review
     * @param documents List of documents to be reviewed
     * @param analysisType Type of analysis to perform (e.g., 'summary', 'detailed', 'compliance')
     * @return Map containing the review results for each document
     */
    Map<String, Object> reviewDocuments(String userId, List<MultipartFile> documents, String analysisType);
}