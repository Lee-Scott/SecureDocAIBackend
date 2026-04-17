package com.familyFirstSoftware.SecureDocAIBackend.service;

import com.familyFirstSoftware.SecureDocAIBackend.dto.ChatMessageResponse;
import com.familyFirstSoftware.SecureDocAIBackend.dtorequest.ChatMessageRequest;

import java.util.List;

public interface ChatService {

    /**
     * Sends a message from the current user and gets a response from the AI agent.
     *
     * @param request The request containing the message content.
     * @return The response from the AI agent.
     */
    /**
     * Retrieves the chat history for the current user.
     *
     * @return A list of chat messages.
     */
    List<ChatMessageResponse> getChatHistory(String aiUserId);

    ChatMessageResponse chat(ChatMessageRequest request);
}
