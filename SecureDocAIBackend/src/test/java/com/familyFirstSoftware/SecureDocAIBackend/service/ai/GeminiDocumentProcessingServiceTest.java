package com.familyFirstSoftware.SecureDocAIBackend.service.ai;

import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeminiDocumentProcessingServiceTest {

    @Mock
    private AiService aiService;

    private DocumentProcessingService documentProcessingService;

    @BeforeEach
    void setUp() {
        documentProcessingService = new GeminiDocumentProcessingService(aiService);
    }

    @Test
    void processDocument_shouldSucceed() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "This is a test document.".getBytes(StandardCharsets.UTF_8)
        );
        when(aiService.generateResponse(anyString())).thenReturn("Summary");

        String result = documentProcessingService.processDocument(file);

        assertEquals("Summary", result.trim());
    }

    @Test
    void processDocument_whenFileEmpty_shouldThrowApiException() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                new byte[0]
        );

        assertThrows(ApiException.class, () -> documentProcessingService.processDocument(file));
    }

    @Test
    void processDocuments_shouldSucceed() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "This is a test document.".getBytes(StandardCharsets.UTF_8)
        );
        when(aiService.generateResponse(anyString())).thenReturn("Summary");

        String result = documentProcessingService.processDocuments(Collections.singletonList(file));

        assertEquals("Summary", result.trim());
    }

    @Test
    void processDocuments_whenTooManyFiles_shouldThrowApiException() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test".getBytes(StandardCharsets.UTF_8)
        );
        List<MultipartFile> files = List.of(file, file, file, file); // 4 files

        assertThrows(ApiException.class, () -> documentProcessingService.processDocuments(files));
    }
}
