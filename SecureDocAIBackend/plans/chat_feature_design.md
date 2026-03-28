# Chat Functionality Design

This document outlines the design for the new chat functionality, which is restricted to users with the `AI_AGENT` role.

## 1. API Endpoint

The chat functionality will be exposed through a single API endpoint:

*   **URL:** `/api/chat/send`
*   **HTTP Method:** `POST`
*   **Description:** Sends a new message to the chat and receives a response from the AI agent.

## 2. Request and Response DTOs

### 2.1. `ChatMessageRequest` DTO

This DTO will be used to send a new message to the chat.

```java
package com.familyFirstSoftware.SecureDocAIBackend.dtorequest;

import javax.validation.constraints.NotEmpty;

public class ChatMessageRequest {

    @NotEmpty(message = "Message content cannot be empty")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
```

### 2.2. `ChatMessageResponse` DTO

This DTO will be used to represent a message in the chat history or a response from the AI agent.

```java
package com.familyFirstSoftware.SecureDocAIBackend.dto;

import java.time.LocalDateTime;

public class ChatMessageResponse {

    private Long id;
    private String content;
    private LocalDateTime timestamp;
    private SenderType sender;

    public enum SenderType {
        USER,
        AI_AGENT
    }

    // Getters and Setters
}
```

## 3. `ChatService` Interface

The `ChatService` will handle the business logic for the chat functionality.

```java
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
    ChatMessageResponse sendMessage(ChatMessageRequest request);

    /**
     * Retrieves the chat history for the current user.
     *
     * @return A list of chat messages.
     */
    List<ChatMessageResponse> getChatHistory();
}
```

## 4. Security Configuration

To restrict the chat endpoint to users with the `AI_AGENT` role, the following security configuration will be required in the `SecurityConfig` class:

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
        // ... other security configurations
        .authorizeRequests()
            .antMatchers("/api/chat/**").hasAuthority("AI_AGENT")
            // ... other endpoint configurations
            .anyRequest().authenticated()
        .and()
        // ... other configurations
}
```

This configuration ensures that only users with the `AI_AGENT` authority can access any URL under `/api/chat/`.
