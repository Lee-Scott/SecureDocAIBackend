package com.familyFirstSoftware.SecureDocAIBackend.service.impl;

import com.familyFirstSoftware.SecureDocAIBackend.dto.chat.ChatMessage;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.chat.ChatMessageEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.chat.ChatRoomEntity;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.chat.MessageType;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.familyFirstSoftware.SecureDocAIBackend.repository.ChatMessageRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.ChatRoomRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.UserRepository;
import com.familyFirstSoftware.SecureDocAIBackend.service.ChatRoomService;
import com.familyFirstSoftware.SecureDocAIBackend.utils.DtoMapper;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/1/2025
 */

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
@Builder
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public ChatRoomEntity createChatRoom(String user1Id, String user2Id) {
        // Check if chat room already exists between these users
        Optional<ChatRoomEntity> existingRoom = chatRoomRepository.findChatRoomBetweenUsers(user1Id, user2Id);
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        // Find users by their IDs
        UserEntity user1 = getUserEntityByUserId(user1Id);
        UserEntity user2 = getUserEntityByUserId(user2Id);

        // Create new chat room
        ChatRoomEntity chatRoom = new ChatRoomEntity();
        chatRoom.setUser1(user1);
        chatRoom.setUser2(user2);
        chatRoom.setIsActive(true);

        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public List<ChatRoomEntity> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

    @Override
    public List<ChatRoomEntity> getChatRoomsForUser(String userId) {
        return chatRoomRepository.findActiveRoomsForUser(userId);
    }

    @Override
    public Optional<ChatRoomEntity> getChatRoomById(String chatRoomId) {
        return chatRoomRepository.findByChatRoomId(chatRoomId);
    }

    @Override
    public ChatMessageEntity sendMessage(String chatRoomId, String senderId, String content, MessageType messageType) {
        // Find chat room
        ChatRoomEntity chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new ApiException("Chat room not found"));

        // Find sender
        UserEntity sender = getUserEntityByUserId(senderId);

        // Create message
        ChatMessageEntity message = new ChatMessageEntity();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());

        return chatMessageRepository.save(message);
    }

    @Override
    public List<ChatMessage> getMessagesForChatRoom(String chatRoomId) {
        // Verify chat room exists
        ChatRoomEntity chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new ApiException("Chat room not found"));

        List<ChatMessageEntity> entities = chatMessageRepository.findMessagesByChatRoomId(chatRoomId);

        return entities.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private ChatMessage convertToDto(ChatMessageEntity entity) {
        return ChatMessage.builder()
                .id(entity.getId())
                .messageId(entity.getMessageId())
                .chatRoomId(entity.getChatRoom().getChatRoomId())
                .sender(DtoMapper.toUserDto(entity.getSender()))
                .content(entity.getContent())
                .messageType(entity.getMessageType())
                .isRead(entity.getIsRead())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private UserEntity getUserEntityByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException("User not found"));
    }
}
