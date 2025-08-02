package com.familyFirstSoftware.SecureDocAIBackend.dto.chat;

import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.chat.MessageType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 7/30/2025
 */

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public class ChatMessage {

    private Long id;
    private String messageId;
    private String chatRoomId;
    private User sender;
    private String content;
    private MessageType messageType;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
