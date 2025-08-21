package com.familyFirstSoftware.SecureDocAIBackend.service.ai;

/**
 * An interface for abstracting communication with various AI models.
 * This allows for easy swapping of AI providers (e.g., Gemini, OpenAI) without changing the core application logic.
 */
public interface AiService {

    /**
     * Generates a response from the AI model based on a user's message.
     *
     * @param userContent The user's message.
     * @return The AI's response as a plain string.
     */
    String generateResponse(String userContent);

}
