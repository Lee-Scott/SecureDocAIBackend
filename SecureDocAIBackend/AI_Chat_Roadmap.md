# AI Chat Integration Roadmap

This document outlines the steps to integrate a Gemini-powered AI chat agent into the SecureDocAI application.

## Phase 1: Foundational Setup

1.  **Gemini API Integration:**
    *   [ ] Add Google AI for Java SDK dependency to `pom.xml`.
    *   [ ] Create a new `GeminiService` to handle communication with the Gemini API.
    *   [ ] Configure the Gemini API key securely in `application.properties`.

2.  **AI User Creation:**
    *   [ ] Locate the database schema or migration script (e.g., a `.sql` file).
    *   [ ] Add a new user with the username `ai_doctor`.
    *   [ ] Assign this user a `DOCTOR` role to enable chat functionality.

## Phase 2: Chat Service Implementation

1.  **Create AI Chat Service:**
    *   [ ] Develop a new service, `AiChatService`, that uses `GeminiService`.
    *   [ ] This service will take user messages from the chat, send them to Gemini, and return the AI's response.

2.  **Integrate with Existing Chat Logic:**
    *   [ ] Modify the existing chat functionality (`ChatService` or similar) to route messages to the `AiChatService` when the recipient is the `ai_doctor`.
    *   [ ] Ensure the AI's responses are saved to the database and sent back to the user in real-time (e.g., via WebSockets).

## Phase 3: Testing and Refinement

1.  **Unit and Integration Testing:**
    *   [ ] Write unit tests for `GeminiService` and `AiChatService`.
    *   [ ] Write integration tests to verify the end-to-end chat flow with the AI agent.

2.  **Prompt Engineering:**
    *   [ ] Refine the prompts sent to Gemini to ensure the AI behaves like a helpful medical professional, maintaining a safe and appropriate tone.
