package com.familyFirstSoftware.SecureDocAIBackend.dtorequest;

import jakarta.validation.constraints.NotEmpty;

public class ChatMessageRequest {

    @NotEmpty(message = "Message content cannot be empty")
    private String content;
    private String documentId;

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
}
