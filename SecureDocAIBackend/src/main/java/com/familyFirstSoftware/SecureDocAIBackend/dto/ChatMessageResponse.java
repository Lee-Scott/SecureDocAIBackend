package com.familyFirstSoftware.SecureDocAIBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {

    private Long id;
    private String content;
    private LocalDateTime timestamp;
    private SenderType sender;

    public enum SenderType {
        USER,
        AI_AGENT
    }
}
