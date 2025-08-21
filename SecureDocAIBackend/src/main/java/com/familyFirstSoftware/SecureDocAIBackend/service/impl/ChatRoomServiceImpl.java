package com.familyFirstSoftware.SecureDocAIBackend.service.impl;

import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.dto.chat.ChatMessage;
import com.familyFirstSoftware.SecureDocAIBackend.dto.chat.ChatRoom;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.chat.ChatMessageEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.chat.ChatRoomEntity;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.chat.MessageType;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.familyFirstSoftware.SecureDocAIBackend.repository.ChatMessageRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.ChatRoomRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.UserRepository;
import com.familyFirstSoftware.SecureDocAIBackend.service.ai.AiService;
import com.familyFirstSoftware.SecureDocAIBackend.service.ChatRoomService;
import com.familyFirstSoftware.SecureDocAIBackend.utils.DtoMapper;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.AI_DOCTOR_EMAIL;

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

    @Qualifier("geminiService")
    private final AiService aiService;

    @Override
    public ChatRoom createChatRoom(String user1Id, String user2Id) {
        // Check if chat room already exists between these users
        Optional<ChatRoomEntity> existingRoom = chatRoomRepository.findChatRoomBetweenUsers(user1Id, user2Id);
        if (existingRoom.isPresent()) {
            return DtoMapper.toChatRoomDto(existingRoom.get());
        }

        // Find users by their IDs
        UserEntity user1 = getUserEntityByUserId(user1Id);
        UserEntity user2 = getUserEntityByUserId(user2Id);

        // Create new chat room
        ChatRoomEntity chatRoom = new ChatRoomEntity();
        chatRoom.setUser1(user1);
        chatRoom.setUser2(user2);
        chatRoom.setIsActive(true);

        return DtoMapper.toChatRoomDto(chatRoomRepository.save(chatRoom));
    }

    @Override
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll()
                .stream()
                .map(DtoMapper::toChatRoomDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatRoom> getChatRoomsForUser(String userId) {
        return chatRoomRepository.findActiveRoomsForUser(userId)
                .stream()
                .map(DtoMapper::toChatRoomDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ChatRoom> getChatRoomById(String chatRoomId) {
        return chatRoomRepository.findByChatRoomId(chatRoomId)
                .map(DtoMapper::toChatRoomDto);
    }

    @Override
    public ChatMessage sendMessage(String chatRoomId, String senderId, String content, MessageType messageType) {
        // Find chat room
        ChatRoomEntity chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new ApiException("Chat room not found"));

        // Find sender
        UserEntity sender = getUserEntityByUserId(senderId);

        // Create and save the user's message
        ChatMessageEntity userMessage = new ChatMessageEntity();
        userMessage.setChatRoom(chatRoom);
        userMessage.setSender(sender);
        userMessage.setContent(content);
        userMessage.setMessageType(messageType);
        userMessage.setIsRead(false);
        userMessage.setCreatedAt(LocalDateTime.now());
        chatMessageRepository.save(userMessage);

        // Determine the recipient and check if it's the AI doctor
        UserEntity recipient = chatRoom.getUser1().getUserId().equals(senderId) ? chatRoom.getUser2() : chatRoom.getUser1();
        if (AI_DOCTOR_EMAIL.equals(recipient.getEmail())) {
            triggerAiResponse(content, chatRoom, recipient);
        }

        return DtoMapper.toChatMessageDto(userMessage);
    }

    private void triggerAiResponse(String userContent, ChatRoomEntity chatRoom, UserEntity aiUser) {
        try {
            String aiContent = aiService.generateResponse(userContent);

            // This runs when the AI response is received
            ChatMessageEntity aiMessage = new ChatMessageEntity();
            aiMessage.setChatRoom(chatRoom);
            aiMessage.setSender(aiUser);
            aiMessage.setContent(aiContent);
            aiMessage.setMessageType(MessageType.TEXT);
            aiMessage.setIsRead(false);
            aiMessage.setCreatedAt(LocalDateTime.now());
            chatMessageRepository.save(aiMessage);
            log.info("Successfully saved AI response to chat room {}", chatRoom.getChatRoomId());

        } catch (Exception e) {
            log.error("Failed to get AI response for chat room {}", chatRoom.getChatRoomId(), e);
            // Optionally, send an error message to the chat room
            ChatMessageEntity errorMessage = new ChatMessageEntity();
            errorMessage.setChatRoom(chatRoom);
            errorMessage.setSender(aiUser);
            errorMessage.setContent("I'm sorry, I encountered an error and couldn't process your request. Please try again later.");
            errorMessage.setMessageType(MessageType.SYSTEM);
            errorMessage.setIsRead(false);
            errorMessage.setCreatedAt(LocalDateTime.now());
            chatMessageRepository.save(errorMessage);
        }
    }

    @Override
    public List<ChatMessage> getMessagesForChatRoom(String chatRoomId) {
        // Verify chat room exists
        ChatRoomEntity chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new ApiException("Chat room not found"));

        List<ChatMessageEntity> entities = chatMessageRepository.findMessagesByChatRoomId(chatRoomId);

        return entities.stream().map(DtoMapper::toChatMessageDto).collect(Collectors.toList());
    }

    public List<User> getHealthcareProviders() {
        return userRepository.findAllByRole_Name("DOCTOR")
                .stream()
                .map(DtoMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public void shareQuestionnaireResults(String chatRoomId, String senderId, String questionnaireId) {
        ChatRoomEntity chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new ApiException("Chat room not found"));

        UserEntity sender = getUserEntityByUserId(senderId);

        // Logic to share questionnaire results (e.g., save a message in the chat room)
        ChatMessageEntity message = new ChatMessageEntity();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setContent("Questionnaire results shared: " + questionnaireId);
        message.setMessageType(MessageType.SYSTEM);
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());

        chatMessageRepository.save(message);
    }

    private UserEntity getUserEntityByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException("User not found"));
    }
}
