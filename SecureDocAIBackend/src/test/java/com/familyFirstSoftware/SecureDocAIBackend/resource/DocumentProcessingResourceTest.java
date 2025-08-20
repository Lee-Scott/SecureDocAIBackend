package com.familyFirstSoftware.SecureDocAIBackend.resource;

import com.familyFirstSoftware.SecureDocAIBackend.service.ai.DocumentProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentProcessingResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentProcessingService documentProcessingService;

    @Test
    @WithMockUser(authorities = "document:read")
    void processDocument_whenAuthenticated_shouldSucceed() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "This is a test document.".getBytes()
        );
        when(documentProcessingService.processDocument(any())).thenReturn("Summary");

        mockMvc.perform(multipart("/api/v1/ai/documents/process").file(file))
                .andExpect(status().isOk());
    }

    @Test
    void processDocument_whenNotAuthenticated_shouldFail() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "This is a test document.".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/ai/documents/process").file(file))
                .andExpect(status().isUnauthorized());
    }
}
