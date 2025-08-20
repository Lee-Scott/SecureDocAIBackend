package com.familyFirstSoftware.SecureDocAIBackend.service.impl;

import com.familyFirstSoftware.SecureDocAIBackend.config.DocumentConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.familyFirstSoftware.SecureDocAIBackend.dto.DocumentVersionDto;
import com.familyFirstSoftware.SecureDocAIBackend.entity.DocumentEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.DocumentVersion;
import com.familyFirstSoftware.SecureDocAIBackend.exception.DocumentOperationException;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ResourceNotFoundException;
import com.familyFirstSoftware.SecureDocAIBackend.repository.DocumentRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.DocumentVersionRepository;
import com.familyFirstSoftware.SecureDocAIBackend.service.AIReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.Date;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Service for handling AI-powered document analysis and reporting.
 * Supports various document formats and integrates with AI services for analysis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIReportServiceImpl implements AIReportService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final ResourceLoader resourceLoader;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final DocumentConfigProperties configProperties;

    @Override
    public CompletableFuture<Map<String, Object>> analyzeDocument(String documentId, String analysisType) {
        return CompletableFuture.supplyAsync(() -> {
            DocumentEntity document = documentRepository.findByDocumentId(documentId)
                    .orElseThrow(() -> DocumentOperationException.builder()
                            .message("Document not found")
                            .status(HttpStatus.NOT_FOUND)
                            .operation("analyzeDocument")
                            .documentId(documentId)
                            .build());

            try {
                // Get the latest version of the document
                DocumentVersion version = versionRepository.findByDocumentIdOrderByCreatedAtDesc(documentId)
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> DocumentOperationException.builder()
                                .message("No document versions found")
                                .status(HttpStatus.NOT_FOUND)
                                .operation("analyzeDocument")
                                .documentId(documentId)
                                .build());

                // Extract text from the document
                String content = extractTextFromDocument(version);
                
                // Generate analysis based on the content
                return generateAnalysis(document, content, analysisType);
                
            } catch (Exception e) {
                log.error("Error analyzing document: {}", documentId, e);
                throw DocumentOperationException.builder()
                        .message("Failed to analyze document: " + e.getMessage())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .operation("analyzeDocument")
                        .documentId(documentId)
                        .cause(e)
                        .build();
            }
        });
    }

    @Override
    public CompletableFuture<Map<String, Object>> analyzeUploadedDocument(MultipartFile file, String analysisType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String content = extractTextFromUploadedFile(file);
                return generateAnalysis(null, content, analysisType);
            } catch (Exception e) {
                log.error("Error analyzing uploaded document", e);
                throw DocumentOperationException.builder()
                        .message("Failed to analyze document: " + e.getMessage())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .operation("analyzeUploadedDocument")
                        .documentId("uploaded-file")
                        .cause(e)
                        .build();
            }
        });
    }

    @Async
    @Override
    public CompletableFuture<Map<String, Object>> generateReport(String documentId, String reportType, String userId) {
        log.info("Generating {} report for documentId: {}, userId: {}", reportType, documentId, userId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentEntity document = documentRepository.findByDocumentId(documentId)
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("Document not found with id: %s", documentId)));

                // Simulate report generation
                Map<String, Object> report = new HashMap<>();
                report.put("reportId", UUID.randomUUID().toString());
                report.put("documentId", documentId);
                report.put("reportType", reportType);
                report.put("generatedBy", userId);
                report.put("generatedAt", java.time.Instant.now());
                report.put("content", "This is a mock " + reportType + " report for document: " + document.getName());
                
                log.info("Generated {} report for documentId: {}", reportType, documentId);
                return report;
                
            } catch (Exception e) {
                log.error("Error generating report for documentId: {}", documentId, e);
                throw new CompletionException(e);
            }
        });
    }

    @Async
    @Override
    public CompletableFuture<Map<String, Object>> sendToAIChat(String documentId, String userId, String aiAgentId, String message) {
        log.info("Sending document {} to AI chat, userId: {}, agentId: {}", documentId, userId, aiAgentId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentEntity document = documentRepository.findByDocumentId(documentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
                
                // In a real implementation, this would send the document to an AI chat service
                Map<String, Object> response = new HashMap<>();
                response.put("message", "This is a mock response from AI agent " + aiAgentId);
                response.put("documentId", documentId);
                response.put("userId", userId);
                response.put("timestamp", new Date());
                
                return response;
            } catch (Exception e) {
                log.error("Error in sendToAIChat for documentId: {}", documentId, e);
                throw new CompletionException(e);
            }
        });
    }

    private String extractTextFromDocument(DocumentVersion version) throws IOException {
        // Implementation for extracting text from different document types
        String filePath = version.getFilePath();
        String extension = FilenameUtils.getExtension(filePath).toLowerCase();
        
        try (InputStream is = resourceLoader.getResource("file:" + filePath).getInputStream()) {
            return switch (extension) {
                case "pdf" -> extractTextFromPdf(is);
                case "docx" -> extractTextFromDocx(is);
                case "txt" -> new String(is.readAllBytes(), StandardCharsets.UTF_8);
                default -> throw new IOException("Unsupported file format: " + extension);
            };
        }
    }

    private String extractTextFromUploadedFile(MultipartFile file) throws IOException {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        
        try (InputStream is = file.getInputStream()) {
            return switch (extension) {
                case "pdf" -> extractTextFromPdf(is);
                case "docx" -> extractTextFromDocx(is);
                case "txt" -> new String(is.readAllBytes(), StandardCharsets.UTF_8);
                default -> throw new IOException("Unsupported file format: " + extension);
            };
        }
    }

    private String extractTextFromPdf(InputStream is) throws IOException {
        try (PDDocument document = PDDocument.load(is)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractTextFromDocx(InputStream is) throws IOException {
        try (XWPFDocument docx = new XWPFDocument(is);
             XWPFWordExtractor extractor = new XWPFWordExtractor(docx)) {
            return extractor.getText();
        }
    }

    private Map<String, Object> generateAnalysis(DocumentEntity document, String content, String analysisType) {
        // In a real implementation, this would call an AI service
        // For now, we'll generate a simple analysis
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("analysisType", analysisType);
        analysis.put("contentLength", content.length());
        analysis.put("wordCount", content.split("\\s+").length);
        analysis.put("summary", generateSummary(content));
        
        // Add document metadata if available
        if (document != null) {
            analysis.put("documentName", document.getName());
            analysis.put("documentType", document.getExtension());
            analysis.put("createdAt", document.getCreatedAt());
        }
        
        return analysis;
    }
    
    private String generateSummary(String content) {
        // Simple summary generation - in a real app, this would use an AI service
        String[] sentences = content.split("[.!?]+");
        return sentences.length > 0 ? sentences[0] : "No content available for summary";
    }
    
    private Map<String, Object> formatReport(Map<String, Object> data, String reportType) {
        // In a real implementation, this would format the report based on the type
        // For now, we'll just return the data as-is
        Map<String, Object> report = new HashMap<>(data);
        report.put("formattedAt", new Date());
        report.put("format", "standard");
        return report;
    }
    
    // Add more helper methods as needed
}
