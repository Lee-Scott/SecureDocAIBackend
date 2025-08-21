package com.familyFirstSoftware.SecureDocAIBackend.service.ai;

import com.google.cloud.vertexai.VertexAI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "SECRET=a-dummy-secret-for-testing",
        "gemini.project.id=test-project",
        "gemini.location=us-central1",
        "gemini.model.name=gemini-pro"
})
class GeminiServiceTest {

    @MockBean
    private VertexAI vertexAI;

    @Autowired
    private AiService aiService;

    @Test
    void contextLoads() {
        assertThat(aiService).isNotNull();
    }
}
