package com.familyFirstSoftware.SecureDocAIBackend.utils;

import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.dto.chat.ChatMessage;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.chat.ChatMessageEntity;
import com.familyFirstSoftware.SecureDocAIBackend.dto.chat.ChatRoom;
import com.familyFirstSoftware.SecureDocAIBackend.entity.chat.ChatRoomEntity;

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

    public static ChatRoom toChatRoomDto(ChatRoomEntity entity) {
        if (entity == null) return null;
        ChatRoom dto = new ChatRoom();
        dto.setId(entity.getId());
        dto.setChatRoomId(entity.getChatRoomId());
        dto.setReferenceId(entity.getReferenceId());
        dto.setUser1(toUserDto(entity.getUser1()));
        dto.setUser2(toUserDto(entity.getUser2()));
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy().toString());
        dto.setUpdatedBy(entity.getUpdatedBy().toString());
        return dto;
    }
}
