package com.familyFirstSoftware.SecureDocAIBackend.dtorequest;

import jakarta.validation.constraints.NotEmpty;

public class ChatMessageRequest {

    @NotEmpty(message = "Message content cannot be empty")
    private String content;
    private String documentId;
    private String aiUserId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getAiUserId() {
        return aiUserId;
    }

    public void setAiUserId(String aiUserId) {
        this.aiUserId = aiUserId;
    }
}
