package com.familyFirstSoftware.SecureDocAIBackend.utils;

import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.dto.chat.ChatMessage;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.chat.ChatMessageEntity;

public class DtoMapper {
    public static User toUserDto(UserEntity entity) {
        if (entity == null) return null;
        User dto = new User();

        // Map only the properties that exist in both classes
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setBio(entity.getBio());
        dto.setImageUrl(entity.getImageUrl());

        // Convert LocalDateTime to String
        if (entity.getLastLogin() != null) dto.setLastLogin(entity.getLastLogin().toString());
        if (entity.getCreatedAt() != null) dto.setCreatedAt(entity.getCreatedAt().toString());
        if (entity.getUpdatedAt() != null) dto.setUpdatedAt(entity.getUpdatedAt().toString());

        // Map role information if available
        if (entity.getRole() != null) {
            dto.setRole(entity.getRole().getName());
            dto.setAuthorities(entity.getRole().getAuthorities().getValue());
        }

        return dto;
    }

    public static ChatMessage toChatMessageDto(ChatMessageEntity entity) {
        if (entity == null) return null;

        return ChatMessage.builder()
                .id(entity.getId())
                .messageId(entity.getMessageId())
                .chatRoomId(entity.getChatRoom().getChatRoomId())
                .sender(toUserDto(entity.getSender()))
                .content(entity.getContent())
                .messageType(entity.getMessageType())
                .isRead(entity.getIsRead())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
