package com.familyFirstSoftware.SecureDocAIBackend.dto.chat;

import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

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
public class ChatRoom {

    private Long id;
    private String chatRoomId;
    private String referenceId;
    private User user1;
    private User user2;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}