package com.familyFirstSoftware.SecureDocAIBackend.service.ai;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.*;

@Service
public class GeminiService implements AiService {

    private final VertexAI vertexAI;
    private final String primaryModelName;
    private final String fallbackModelName;
    private final GenerationConfig generationConfig;
    private final java.util.List<SafetySetting> safetySettings;
    private final Content systemInstruction;
    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    public GeminiService(
            VertexAI vertexAI) throws IOException {

        this.vertexAI = vertexAI;
        this.primaryModelName = GEMINI_2_5_FLASH;
        this.fallbackModelName = GEMINI_2_5_FLASH_LITE;

        this.systemInstruction = Content.newBuilder()
                .addParts(Part.newBuilder().setText(GEMINI_DOCTOR_PROMPT).build())
                .build();

        this.safetySettings = Collections.singletonList(
                SafetySetting.newBuilder()
                        .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                        .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_ONLY_HIGH)
                        .build());

        this.generationConfig = GenerationConfig.newBuilder()
                .setTemperature(0.4f)
                .setTopK(32)
                .setTopP(1.0f)
                .setMaxOutputTokens(2048)
                .build();
    }

    @Override
    public String generateResponse(String userContent) {
        try {
            return callModel(this.primaryModelName, userContent);
        } catch (Exception primaryEx) {
            log.warn("Primary model '{}' failed: {}. Falling back to '{}'", this.primaryModelName, primaryEx.getMessage(), this.fallbackModelName);
            try {
                return callModel(this.fallbackModelName, userContent);
            } catch (Exception fallbackEx) {
                log.error("Fallback model '{}' also failed: {}", this.fallbackModelName, fallbackEx.getMessage(), fallbackEx);
                throw new RuntimeException("Failed to generate AI response after fallback", fallbackEx);
            }
        }
    }

    private String callModel(String modelName, String userContent) throws IOException {
        log.info("Calling Gemini model '{}' with input length={} chars", modelName, userContent != null ? userContent.length() : 0);
        GenerativeModel model = new GenerativeModel(modelName, this.vertexAI)
                .withSystemInstruction(this.systemInstruction)
                .withSafetySettings(this.safetySettings)
                .withGenerationConfig(this.generationConfig);

        GenerateContentResponse response = model.generateContent(userContent);
        return ResponseHandler.getText(response);
    }
}
