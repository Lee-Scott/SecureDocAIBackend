package com.familyFirstSoftware.SecureDocAIBackend.service.impl;

import com.familyFirstSoftware.SecureDocAIBackend.dto.Document;
import com.familyFirstSoftware.SecureDocAIBackend.dto.DocumentVersionDto;
import com.familyFirstSoftware.SecureDocAIBackend.dto.api.IDocument;
import com.familyFirstSoftware.SecureDocAIBackend.entity.DocumentEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.DocumentLock;
import com.familyFirstSoftware.SecureDocAIBackend.entity.DocumentVersion;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.familyFirstSoftware.SecureDocAIBackend.repository.DocumentLockRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.DocumentRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.DocumentVersionRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.UserRepository;
import com.familyFirstSoftware.SecureDocAIBackend.service.DocumentService;
import com.familyFirstSoftware.SecureDocAIBackend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.familyFirstSoftware.SecureDocAIBackend.utils.DocumentUtils.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.util.StringUtils.cleanPath;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 4/14/2025
 */

@Slf4j
@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentLockRepository documentLockRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    
    private static final String VERSIONS_DIR = "versions";
    private static final String FILE_STORAGE = System.getProperty("user.home") + "/uploads/";
    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);


    @Override
    public Page<IDocument> getDocuments(int page, int size) {
        return documentRepository.findDocuments(of(page, size, Sort.by("name")));
    }

    @Override
    @Transactional
    public Document reattachDocument(String documentId, MultipartFile file) {
        logger.info("Reattaching file to document: {}", documentId);
        
        // 1. Verify document exists
        DocumentEntity documentEntity = documentRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new ApiException("Document not found with ID: " + documentId));
        
        // 2. Clean and validate the new filename
        String filename = cleanPath(Objects.requireNonNull(file.getOriginalFilename(), "File name cannot be null"));
        if (filename.contains("..")) {
            throw new ApiException("Invalid file name (contains '..'): " + filename);
        }
        
        // 3. Get the storage path
        var storage = Paths.get(FILE_STORAGE).toAbsolutePath().normalize();
        Path targetLocation = storage.resolve(filename);
        
        try {
            // 4. Save the new file
            Files.copy(file.getInputStream(), targetLocation, REPLACE_EXISTING);
            
            // 5. Update document metadata
            documentEntity.setName(filename);
            documentEntity.setExtension(getExtension(filename));
            documentEntity.setUri(getDocumentUri(filename));
            documentEntity.setFormattedSize(byteCountToDisplaySize(file.getSize()));
            documentEntity.setIcon(setIcon(getExtension(filename)));
            documentEntity.setMimeType(file.getContentType());
            documentEntity.setSize(file.getSize());
            
            // 6. Save the updated document
            DocumentEntity savedEntity = documentRepository.save(documentEntity);
            
            // 7. Return the updated document
            return fromDocumentEntity(
                savedEntity,
                userService.getUserById(savedEntity.getCreatedBy()),
                userService.getUserById(savedEntity.getUpdatedBy())
            );
            
        } catch (IOException e) {
            String errorMsg = String.format("Failed to reattach file '%s' to document %s: %s", 
                filename, documentId, e.getMessage());
            logger.error(errorMsg, e);
            throw new ApiException(errorMsg);
        }
    }
    
    @Override
    public Page<IDocument> getDocuments(int page, int size, String name) {
        return documentRepository.findDocumentsByName(name, PageRequest.of(page, size, Sort.by("name")));
    }

    @Override
    public Collection<Document> saveDocuments(String userId, List<MultipartFile> documents) {
        List<Document> newDocuments = new ArrayList<>();
        logger.info("Starting document upload for user: {}", userId);
        
        try {
            // Verify user exists
            var userEntity = userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new ApiException("User not found with ID: " + userId));
            
            // Create upload directory if it doesn't exist
            var storage = Paths.get(FILE_STORAGE).toAbsolutePath().normalize();
            if (!Files.exists(storage)) {
                logger.info("Creating upload directory: {}", storage);
                Files.createDirectories(storage);
            }
            
            logger.info("Uploading {} documents to: {}", documents.size(), storage);
            
            for(MultipartFile document : documents) {
                String filename = null;
                try {
                    // Validate and clean filename
                    filename = cleanPath(Objects.requireNonNull(document.getOriginalFilename(), "File name cannot be null"));
                    logger.debug("Processing file: {}", filename);
                    
                    if (filename.contains("..")) {
                        throw new ApiException("Invalid file name (contains '..'): " + filename);
                    }
                    
                    // Create document entity
                    var documentEntity = DocumentEntity.builder()
                            .documentId(UUID.randomUUID().toString())
                            .name(filename)
                            .owner(userEntity)
                            .extension(getExtension(filename))
                            .uri(getDocumentUri(filename))
                            .formattedSize(byteCountToDisplaySize(document.getSize()))
                            .icon(setIcon(getExtension(filename)))
                            .mimeType(document.getContentType())
                            .size(document.getSize())
                            .build();
                    
                    // Save document metadata to database
                    logger.debug("Saving document metadata: {}", filename);
                    var savedDocument = documentRepository.save(documentEntity);
                    
                    // Save file to disk
                    Path targetLocation = storage.resolve(filename);
                    logger.debug("Saving file to: {}", targetLocation);
                    Files.copy(document.getInputStream(), targetLocation, REPLACE_EXISTING);
                    
                    // Create response DTO
                    Document newDocument = fromDocumentEntity(
                        savedDocument, 
                        userService.getUserById(savedDocument.getCreatedBy()), 
                        userService.getUserById(savedDocument.getUpdatedBy())
                    );
                    newDocuments.add(newDocument);
                    logger.info("Successfully saved document: {} (ID: {})", filename, savedDocument.getDocumentId());
                    
                } catch (Exception e) {
                    String errorMsg = String.format("Error processing file '%s': %s", 
                        filename != null ? filename : "unknown", e.getMessage());
                    logger.error(errorMsg, e);
                    throw new ApiException(errorMsg);
                }
            }
            
            logger.info("Successfully uploaded {} documents", newDocuments.size());
            return newDocuments;
            
        } catch (ApiException e) {
            // Re-throw our custom exceptions as-is
            throw e;
        } catch (Exception e) {
            String errorMsg = "Unexpected error during document upload: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new ApiException(errorMsg);
        }
    }


    // Todo create a separate table that logs all actions related to documents
    @Override
    @Transactional
    public IDocument updateDocument(String documentId, String name, String description) {
        try {
            // Get the existing document
            var documentEntity = getDocumentEntity(documentId);
            
            // Create a version before updating
            createNewVersion(documentEntity, "Document updated", documentEntity.getUpdatedBy());
            
            // Update the document
            var document = Paths.get(FILE_STORAGE).resolve(documentEntity.getName()).toAbsolutePath().normalize();
            if (!documentEntity.getName().equals(name)) {
                // Only move/rename if the name has actually changed
                Path newPath = document.resolveSibling(name);
                Files.move(document, newPath, REPLACE_EXISTING);
                documentEntity.setName(name);
            }
            
            documentEntity.setDescription(description);
            documentEntity = documentRepository.save(documentEntity);
            
            return getDocumentByDocumentId(documentId);
        } catch (Exception exception) {
            log.error("Error updating document: {}", exception.getMessage(), exception);
            throw new ApiException("Unable to update document: " + exception.getMessage());
        }
    }

    private DocumentEntity getDocumentEntity(String documentId) {
        return documentRepository.findByDocumentId(documentId).orElseThrow(() -> new ApiException("Document not found"));
    }


    @Override
    public IDocument getDocumentByDocumentId(String documentId) {
        logger.info("Looking up document with ID: {}", documentId);
        try {
            Optional<IDocument> documentOpt = documentRepository.findDocumentByDocumentId(documentId);
            if (documentOpt.isPresent()) {
                logger.info("Successfully found document with ID: {}", documentId);
                return documentOpt.get();
            } else {
                logger.warn("No document found with ID: {}", documentId);
                // Try to find if document exists with different case
                List<DocumentEntity> similarDocuments = documentRepository.findAll()
                    .stream()
                    .filter(doc -> doc.getDocumentId().equalsIgnoreCase(documentId))
                    .collect(Collectors.toList());
                
                if (!similarDocuments.isEmpty()) {
                    logger.warn("Found document with matching ID but different case. Requested: {}, Found: {}", 
                        documentId, similarDocuments.get(0).getDocumentId());
                }
                
                throw new ApiException("Document not found with ID: " + documentId);
            }
        } catch (Exception e) {
            logger.error("Error retrieving document with ID: " + documentId, e);
            throw new ApiException("Error retrieving document: " + e.getMessage());
        }
    }

    @Override
    public Resource getResource(String documentId, String version) throws IOException {
        if (version != null) {
            // Get specific version
            DocumentVersion docVersion = documentVersionRepository.findByDocumentIdAndVersionNumber(documentId, version)
                .orElseThrow(() -> new ApiException("Version not found"));
            
            Path filePath = Paths.get(docVersion.getFilePath());
            if (!Files.exists(filePath)) {
                throw new ApiException("Version file not found");
            }
            return new UrlResource(filePath.toUri());
        }
        
        // Get current version
        logger.info("Looking up document with ID: {}", documentId);
        IDocument document = getDocumentByDocumentId(documentId);
        logger.info("Found document: {}", document.getName());
        
        var filePath = Paths.get(FILE_STORAGE).toAbsolutePath().normalize().resolve(document.getName());
        logger.info("Resolved file path: {}", filePath);
        
        if (!Files.exists(filePath)) {
            logger.error("File not found at path: {}", filePath);
            // List files in the directory for debugging
            try (Stream<Path> files = Files.list(Paths.get(FILE_STORAGE).toAbsolutePath().normalize())) {
                logger.info("Files in upload directory:");
                files.forEach(f -> logger.info("- {}", f.getFileName()));
            } catch (IOException e) {
                logger.error("Error listing files in upload directory", e);
            }
            throw new ApiException("Document file not found on server");
        }
        return new UrlResource(filePath.toUri());
    }
    

    public Resource getResource(String documentName) {
        try {
            return getResource(documentName, null);
        } catch (IOException e) {
            throw new ApiException("Unable to download document: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteDocument(String documentId) {
        try {
            DocumentEntity documentEntity = getDocumentEntity(documentId);
            // Delete file from filesystem
            var filePath = Paths.get(FILE_STORAGE).toAbsolutePath().normalize().resolve(documentEntity.getName());
            Files.deleteIfExists(filePath);
            // Delete database record
            documentRepository.delete(documentEntity);
        } catch (IOException exception) {
            throw new ApiException("Unable to delete document file: " + exception.getMessage());
        } catch (Exception exception) {
            throw new ApiException("Unable to delete document: " + exception.getMessage());
        }
    }

    @Override
    public Map<String, Object> getDocumentStatus(String documentId) {
        Map<String, Object> status = new HashMap<>();
        status.put("documentId", documentId);
        
        // Check if document exists
        Optional<DocumentEntity> docOpt = documentRepository.findByDocumentId(documentId);
        if (docOpt.isEmpty()) {
            status.put("status", "not_found");
            status.put("message", "Document not found");
            return status;
        }
        
        // Check lock status
        Optional<DocumentLock> lockOpt = documentLockRepository.findByDocumentId(documentId);
        if (lockOpt.isPresent()) {
            DocumentLock lock = lockOpt.get();
            boolean isExpired = lock.getExpiresAt().isBefore(LocalDateTime.now());
            
            status.put("isLocked", !isExpired);
            status.put("lockStatus", isExpired ? "expired" : "locked");
            status.put("lockedBy", lock.getUserId());
            status.put("lockedAt", lock.getLockTimestamp());
            status.put("expiresAt", lock.getExpiresAt());
            
            if (isExpired) {
                // Clean up expired lock
                documentLockRepository.delete(lock);
            }
        } else {
            status.put("isLocked", false);
            status.put("lockStatus", "unlocked");
        }
        
        // Add basic document info
        DocumentEntity doc = docOpt.get();
        status.put("documentName", doc.getName());
        status.put("createdAt", doc.getCreatedAt());
        status.put("lastModified", doc.getUpdatedAt());
        
        return status;
    }
    @Override
    public List<DocumentVersionDto> getDocumentVersions(String documentId) {
        // Check if document exists
        if (!documentRepository.existsByDocumentId(documentId)) {
            throw new ApiException("Document not found");
        }
        
        return documentVersionRepository.findByDocumentIdOrderByCreatedAtDesc(documentId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> reviewDocuments(String userId, List<MultipartFile> documents, String analysisType) {
        Map<String, Object> results = new HashMap<>();
        List<Map<String, Object>> documentReviews = new ArrayList<>();
        
        for (MultipartFile file : documents) {
            try {
                String filename = cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                String content = new String(file.getBytes());
                
                // Process document based on file type
                String fileExtension = FilenameUtils.getExtension(filename).toLowerCase();
                String processedContent = processDocumentContent(content, fileExtension);
                
                // Get AI review based on analysis type
                String aiReview = getAIReview(processedContent, analysisType);
                
                // Store results
                Map<String, Object> docResult = new HashMap<>();
                docResult.put("filename", filename);
                docResult.put("fileSize", byteCountToDisplaySize(file.getSize()));
                docResult.put("contentType", file.getContentType());
                docResult.put("analysisType", analysisType);
                docResult.put("review", aiReview);
                
                documentReviews.add(docResult);
                
                logger.info("Successfully processed document: {}", filename);
                
            } catch (Exception e) {
                logger.error("Error processing document: " + file.getOriginalFilename(), e);
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("filename", file.getOriginalFilename());
                errorResult.put("error", "Failed to process document: " + e.getMessage());
                documentReviews.add(errorResult);
            }
        }
        
        results.put("documents", documentReviews);
        results.put("timestamp", LocalDateTime.now().toString());
        results.put("totalDocumentsProcessed", documentReviews.size());
        results.put("successfulDocuments", documentReviews.stream()
                .filter(doc -> !doc.containsKey("error"))
                .count());
        
        return results;
    }
    
    private String processDocumentContent(String content, String fileExtension) {
        // For text-based files, we can use the content as-is
        if (Set.of("txt", "md", "json", "xml", "html", "css", "js").contains(fileExtension)) {
            return content;
        }
        
        // For binary files, we might want to process them differently
        // This is a simplified version - in a real application, you might want to use
        // libraries like Apache Tika for better content extraction
        return "[Content processing not fully implemented for ." + fileExtension + " files]";
    }
    
    private String getAIReview(String content, String analysisType) {
        // In a real implementation, this would call an AI service
        // For now, we'll return a mock response
        String review;
        
        switch (analysisType.toLowerCase()) {
            case "summary":
                review = "Summary of the document: " + content.substring(0, Math.min(200, content.length())) + "...";
                break;
            case "detailed":
                review = "Detailed analysis of the document. Key points:\n" +
                         "1. Document contains " + content.length() + " characters\n" +
                         "2. Estimated reading time: " + (content.length() / 1000) + " minutes\n" +
                         "3. Sample content: " + content.substring(0, Math.min(100, content.length())) + "...";
                break;
            case "compliance":
                review = "Compliance check results:\n" +
                         "- Document appears to be " + 
                         (content.toLowerCase().contains("confidential") ? "confidential" : "non-confidential") + "\n" +
                         "- Contains " + content.split("\\s+").length + " words\n" +
                         "- Estimated review time: " + (content.length() / 2000) + " minutes";
                break;
            default:
                review = "Analysis type '" + analysisType + "' not recognized. Using default summary.\n" +
                         content.substring(0, Math.min(150, content.length())) + "...";
        }
        
        return review;
    }
    
    private DocumentVersionDto convertToDto(DocumentVersion version) {
        return DocumentVersionDto.builder()
                .id(version.getId())
                .documentId(version.getDocumentId())
                .filePath(version.getFilePath())
                .versionNumber(version.getVersionNumber())
                .createdAt(version.getCreatedAt())
                .createdBy(version.getCreatedBy())
                .size(version.getSize())
                .mimeType(version.getMimeType())
                .checksum(version.getChecksum())
                .changeDescription(version.getChangeDescription())
                .build();
    }
    
    private void createNewVersion(DocumentEntity document, String changeDescription, Long userId) throws IOException {
        Path sourcePath = Paths.get(document.getFilePath());
        String versionDir = FILE_STORAGE + VERSIONS_DIR + "/" + document.getDocumentId() + "/";
        
        // Create versions directory if it doesn't exist
        Files.createDirectories(Paths.get(versionDir));
        
        // Generate version number (e.g., v1, v2, etc.)
        long versionCount = documentVersionRepository.countByDocumentId(document.getDocumentId()) + 1;
        String versionNumber = "v" + versionCount;
        
        // Create versioned filename
        String originalFilename = Paths.get(document.getFilePath()).getFileName().toString();
        String baseName = FilenameUtils.getBaseName(originalFilename);
        String extension = FilenameUtils.getExtension(originalFilename);
        String versionedFilename = String.format("%s_%s.%s", baseName, versionNumber, extension);
        
        // Copy file to versioned location
        Path targetPath = Paths.get(versionDir + versionedFilename);
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        // Calculate file checksum
        String checksum = calculateChecksum(targetPath);
        
        // Create and save version record
        DocumentVersion version = DocumentVersion.builder()
                .documentId(document.getDocumentId())
                .filePath(targetPath.toString())
                .versionNumber(versionNumber)
                .size(document.getSize())
                .mimeType(document.getMimeType())
                .checksum(checksum)
                .createdBy(userId)
                .changeDescription(changeDescription)
                .build();
                
        documentVersionRepository.save(version);
    }
    
    private String calculateChecksum(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath)) {
            return DigestUtils.md5Hex(is);
        }
    }
    
    @Override
    public Map<String, Object> checkinDocument(String documentId, String userId) {
        // Check if document exists
        if (!documentRepository.existsByDocumentId(documentId)) {
            throw new ApiException("Document not found");
        }
        
        // Get existing lock
        Optional<DocumentLock> lockOpt = documentLockRepository.findByDocumentId(documentId);
        
        if (lockOpt.isEmpty()) {
            return Map.of(
                "documentId", documentId,
                "lockStatus", "none",
                "message", "No active lock found for this document"
            );
        }
        
        DocumentLock lock = lockOpt.get();
        
        // Check if lock is held by another user
        if (!lock.getUserId().equals(userId)) {
            return Map.of(
                "documentId", documentId,
                "lockedBy", lock.getUserId(),
                "lockStatus", "unavailable",
                "message", "Document is locked by another user"
            );
        }
        
        // Delete the lock
        documentLockRepository.delete(lock);
        
        // Return success response
        return Map.of(
            "documentId", documentId,
            "userId", userId,
            "lockStatus", "released",
            "message", "Document checked in successfully"
        );
    }
    
    @Override
    public Map<String, Object> refreshDocumentLock(String documentId, String userId) {
        // Check if document exists
        if (!documentRepository.existsByDocumentId(documentId)) {
            throw new ApiException("Document not found");
        }
        
        // Get existing lock
        DocumentLock lock = documentLockRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new ApiException("No active lock found for this document"));

        // Verify the user owns the lock
        if (!lock.getUserId().equals(userId)) {
            throw new ApiException("You don't have permission to refresh this lock");
        }

        // Extend the lock
        lock.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        documentLockRepository.save(lock);

        return Map.of(
            "documentId", lock.getDocumentId(),
            "userId", lock.getUserId(),
            "lockStatus", "refreshed",
            "expiresAt", lock.getExpiresAt(),
            "message", "Document lock refreshed successfully"
        );
    }
    
    @Override
    public Object checkoutDocument(String documentId, String userId) {
        // Verify document exists
        if (!documentRepository.existsByDocumentId(documentId)) {
            throw new ApiException("Document not found");
        }
        
        // Verify user exists
        if (!userRepository.findByUserId(userId).isPresent()) {
            throw new ApiException("User not found");
        }

        Optional<DocumentLock> existingLock = documentLockRepository.findByDocumentId(documentId);
        if (existingLock.isPresent()) {
            DocumentLock lock = existingLock.get();
            if (lock.getUserId().equals(userId)) {
                // Extend/refresh lock
                lock.setExpiresAt(LocalDateTime.now().plusMinutes(10));
                documentLockRepository.save(lock);
                return Map.of(
                        "documentId", lock.getDocumentId(),
                        "userId", lock.getUserId(),
                        "lockStatus", "extended",
                        "expiresAt", lock.getExpiresAt()
                );
            } else {
                // Document is locked by another user
                return Map.of(
                        "documentId", documentId,
                        "lockedBy", lock.getUserId(),
                        "lockStatus", "unavailable"
                );
            }
        } else {
            // Create new lock
            DocumentLock newLock = DocumentLock.builder()
                    .documentId(documentId)
                    .userId(userId)
                    .lockTimestamp(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusMinutes(10))
                    .build();
            documentLockRepository.save(newLock);
            return Map.of(
                    "documentId", newLock.getDocumentId(),
                    "userId", newLock.getUserId(),
                    "lockStatus", "acquired",
                    "expiresAt", newLock.getExpiresAt()
            );
        }
    }
}