package com.familyFirstSoftware.SecureDocAIBackend.service.test;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeminiTestService {

    private final String projectId;
    private final String location;
    private final String modelId;
    private final boolean useMock;

    @Autowired
    public GeminiTestService(Environment env) {
        this.projectId = env.getProperty("gemini.project.id");
        this.location = env.getProperty("gemini.location");
        this.modelId = env.getProperty("GEMINI_MODEL");
        this.useMock = Boolean.parseBoolean(env.getProperty("AI_TEST_MOCK"));
    }

    public Map<String, String> callGemini(String message) throws IOException {
        if (useMock) {
            Map<String, String> mockResponse = new HashMap<>();
            mockResponse.put("prompt", message);
            mockResponse.put("response", "Hello — test endpoint responding.");
            mockResponse.put("raw", "mock");
            return mockResponse;
        }

        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            GenerationConfig generationConfig =
                    GenerationConfig.newBuilder()
                            .setMaxOutputTokens(256)
                            .setTemperature(0.0f)
                            .build();

            GenerativeModel model = new GenerativeModel(modelId, vertexAI)
                    .withGenerationConfig(generationConfig);

            String responseText = ResponseHandler.getText(model.generateContent(message));

            Map<String, String> response = new HashMap<>();
            response.put("prompt", message);
            response.put("response", responseText);
            response.put("raw", responseText);
            return response;
        }
    }
}
