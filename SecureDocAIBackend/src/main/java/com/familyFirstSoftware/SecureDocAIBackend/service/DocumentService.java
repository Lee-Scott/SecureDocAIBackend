package com.familyFirstSoftware.SecureDocAIBackend.service;



import com.familyFirstSoftware.SecureDocAIBackend.dto.Document;
import com.familyFirstSoftware.SecureDocAIBackend.dto.api.IDocument;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

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
    IDocument updateDocument(String documentId, String name, String description);
    void deleteDocument(String documentId);
    IDocument getDocumentByDocumentId(String documentId);
    Resource getResource(String documentName);
}