package com.familyFirstSoftware.SecureDocAIBackend.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service for handling AI-powered document analysis and reporting.
 */
public interface AIReportService {
    
    /**
     * Analyzes a document by its ID using the specified analysis type.
     *
     * @param documentId The ID of the document to analyze
     * @param analysisType The type of analysis to perform (e.g., "summary", "detailed", "compliance")
     * @return A CompletableFuture containing the analysis results
     */
    CompletableFuture<Map<String, Object>> analyzeDocument(String documentId, String analysisType);
    
    /**
     * Analyzes an uploaded document file.
     *
     * @param file The uploaded file to analyze
     * @param analysisType The type of analysis to perform
     * @return A CompletableFuture containing the analysis results
     */
    CompletableFuture<Map<String, Object>> analyzeUploadedDocument(
            org.springframework.web.multipart.MultipartFile file, 
            String analysisType
    );
    
    /**
     * Generates a report for a document.
     *
     * @param documentId The ID of the document
     * @param reportType The type of report to generate
     * @param userId The ID of the user requesting the report
     * @return A CompletableFuture containing the generated report
     */
    CompletableFuture<Map<String, Object>> generateReport(
            String documentId, 
            String reportType, 
            String userId
    );
    
    /**
     * Sends a document to an AI chat for analysis and interaction.
     *
     * @param documentId The ID of the document to send
     * @param userId The ID of the user sending the document
     * @param aiAgentId The ID of the AI agent to interact with
     * @param message Optional message to include with the document
     * @return A CompletableFuture containing the chat response
     */
    CompletableFuture<Map<String, Object>> sendToAIChat(
            String documentId, 
            String userId, 
            String aiAgentId, 
            String message
    );
}
