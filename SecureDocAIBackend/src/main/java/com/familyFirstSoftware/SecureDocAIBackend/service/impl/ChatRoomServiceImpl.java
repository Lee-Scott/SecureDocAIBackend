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
    private final com.familyFirstSoftware.SecureDocAIBackend.service.DocumentService documentService;

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
        ChatRoomEntity chatRoom = ChatRoomEntity.builder().build();
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
    public ChatMessage sendMessage(String chatRoomId, String senderId, String content, MessageType messageType, String documentId) {
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

        // Determine the recipient and check if it's an AI persona
        UserEntity recipient = chatRoom.getUser1().getUserId().equals(senderId) ? chatRoom.getUser2() : chatRoom.getUser1();
        if (recipient.getEmail() != null && recipient.getEmail().endsWith("_ai@familyfirstsoftware.com")) {
            triggerAiResponse(content, chatRoom, recipient, documentId);
        }

        return DtoMapper.toChatMessageDto(userMessage);
    }

    private void triggerAiResponse(String userContent, ChatRoomEntity chatRoom, UserEntity aiUser, String documentId) {
        try {
            List<ChatMessageEntity> history = chatMessageRepository.findMessagesByChatRoomId(chatRoom.getChatRoomId());
            StringBuilder promptBuilder = new StringBuilder();
            
            // Extract document ID from userContent if documentId is null or empty
            if (documentId == null || documentId.trim().isEmpty()) {
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\[ID:\\s*([a-fA-F0-9\\-]+)\\]");
                java.util.regex.Matcher matcher = pattern.matcher(userContent);
                if (matcher.find()) {
                    documentId = matcher.group(1);
                }
            }

            if (!history.isEmpty()) {
                promptBuilder.append("Previous Conversation History:\n");
                int startIndex = Math.max(0, history.size() - 10);
                for (int i = startIndex; i < history.size() - 1; i++) { // -1 to skip the current message that was just saved
                    ChatMessageEntity msg = history.get(i);
                    String role = aiUser.getUserId().equals(msg.getSender().getUserId()) ? "AI Doctor" : "User";
                    promptBuilder.append(role).append(": ").append(msg.getContent()).append("\n");
                }
                promptBuilder.append("\n");
            }
            
            if (documentId != null && !documentId.trim().isEmpty()) {
                if (!documentService.documentExists(documentId)) {
                    throw new jakarta.persistence.EntityNotFoundException("Document with ID " + documentId + " not found in the database.");
                }
                try {
                    org.springframework.core.io.Resource resource = documentService.getResource(documentId, null);
                    try (java.io.InputStream stream = resource.getInputStream()) {
                        org.apache.tika.sax.BodyContentHandler handler = new org.apache.tika.sax.BodyContentHandler(-1);
                        org.apache.tika.metadata.Metadata metadata = new org.apache.tika.metadata.Metadata();
                        org.apache.tika.parser.AutoDetectParser parser = new org.apache.tika.parser.AutoDetectParser();
                        parser.parse(stream, handler, metadata, new org.apache.tika.parser.ParseContext());
                        String documentText = handler.toString();
                        promptBuilder.append("\n\n--- DOCUMENT CONTENT ---\n")
                                .append(documentText).append("\n--- END DOCUMENT CONTENT ---\n\n");
                    }
                } catch (Exception e) {
                    log.error("Failed to extract text for document {}", documentId, e);
                    promptBuilder.append("[Note: Failed to load document context.]\n\n");
                }
            }
            
            promptBuilder.append("User's new message:\n").append(userContent);
            
            String prompt = promptBuilder.toString();
            log.info("Sending prompt to Gemini AI for chat room {}:\n{}", chatRoom.getChatRoomId(), prompt);
            String aiContent = aiService.generateResponse(prompt);
            log.info("Received response from Gemini AI for chat room {}:\n{}", chatRoom.getChatRoomId(), aiContent);

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

    @Override
    public void deleteChatRoom(String chatRoomId) {
        ChatRoomEntity chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new ApiException("Chat room not found"));
        
        // Delete all associated messages first to prevent foreign key constraint violations
        List<ChatMessageEntity> messages = chatMessageRepository.findByChatRoom(chatRoom);
        if (!messages.isEmpty()) {
            chatMessageRepository.deleteAll(messages);
        }
        
        chatRoomRepository.delete(chatRoom);
    }

    private UserEntity getUserEntityByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException("User not found"));
    }
}
