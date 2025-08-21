package com.familyFirstSoftware.SecureDocAIBackend.service.ai;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Service for processing documents with an AI model.
 * This interface defines the core functionalities for handling single and multiple document processing,
 * including validation, content extraction, and summarization.
 *
 * @author Your Name
 * @since 2024-07-25
 */
public interface DocumentProcessingService {

    /**
     * Processes a single document file, extracts its content, and generates a summary using an AI model.
     *
     * @param file The multipart file representing the uploaded document.
     * @return A string containing the AI-generated summary of the document.
     */
    String processDocument(MultipartFile file);

    /**
     * Processes a list of document files in batch, extracts their content, and generates a consolidated summary.
     *
     * @param files A list of multipart files to be processed.
     * @return A string containing the AI-generated summary for all the documents.
     */
    String processDocuments(List<MultipartFile> files);

    /**
     * Retrieves the configuration limits for document processing.
     * This can include maximum file size, supported file types, and other constraints.
     *
     * @return A map containing the configuration limits.
     */
    default Map<String, Object> getProcessingLimits() {
        return Map.of(
                "maxFileSize", "50MB",
                "supportedFileTypes", List.of("PDF", "DOCX", "DOC", "TXT", "RTF"),
                "maxFileCount", 3,
                "maxPagesPerDocument", 1000
        );
    }
}
