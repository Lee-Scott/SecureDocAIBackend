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

// ChatLobby.java
@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ChatLobby {
    private Long id;
    private String lobbyId;
    private String name;
    private String description;
    private Integer maxParticipants;
    private Boolean isPublic;
    private Boolean isActive;
    private Integer currentParticipants;
    private LocalDateTime createdAt;
    private User createdBy;
}
