package com.familyFirstSoftware.SecureDocAIBackend.service.impl;

import com.familyFirstSoftware.SecureDocAIBackend.dto.ChatMessageResponse;
import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.dtorequest.ChatMessageRequest;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.chat.ChatMessageEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.chat.ChatRoomEntity;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.chat.MessageType;
import com.familyFirstSoftware.SecureDocAIBackend.repository.ChatMessageRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.ChatRoomRepository;
import com.familyFirstSoftware.SecureDocAIBackend.repository.UserRepository;
import com.familyFirstSoftware.SecureDocAIBackend.service.ChatService;
import com.familyFirstSoftware.SecureDocAIBackend.service.UserService;
import com.familyFirstSoftware.SecureDocAIBackend.service.ai.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.AI_DOCTOR_EMAIL;
import static com.familyFirstSoftware.SecureDocAIBackend.dto.ChatMessageResponse.SenderType.AI_AGENT;
import static com.familyFirstSoftware.SecureDocAIBackend.dto.ChatMessageResponse.SenderType.USER;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final GeminiService geminiService;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final UserService userService;


    @Override
    public ChatMessageResponse sendMessage(ChatMessageRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity userEntity = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        UserEntity aiUserEntity = userRepository.findUserByEmailIgnoreCase(AI_DOCTOR_EMAIL).orElseThrow(() -> new RuntimeException("AI User not found"));
        
        ChatRoomEntity chatRoom = chatRoomRepository.findChatRoomBetweenUsers(userEntity.getUserId(), aiUserEntity.getUserId()).orElseGet(() -> {
            ChatRoomEntity newChatRoom = new ChatRoomEntity();
            newChatRoom.setUser1(userEntity);
            newChatRoom.setUser2(aiUserEntity);
            return chatRoomRepository.save(newChatRoom);
        });

        ChatMessageEntity userMessage = new ChatMessageEntity();
        userMessage.setChatRoom(chatRoom);
        userMessage.setContent(request.getContent());
        userMessage.setCreatedAt(LocalDateTime.now());
        userMessage.setSender(userEntity);
        userMessage.setMessageType(MessageType.TEXT);
        chatMessageRepository.save(userMessage);

        String aiResponse = geminiService.generateResponse(request.getContent());

        ChatMessageEntity aiMessage = new ChatMessageEntity();
        aiMessage.setChatRoom(chatRoom);
        aiMessage.setContent(aiResponse);
        aiMessage.setCreatedAt(LocalDateTime.now());
        aiMessage.setSender(aiUserEntity);
        aiMessage.setMessageType(MessageType.TEXT);
        chatMessageRepository.save(aiMessage);

        return new ChatMessageResponse(aiMessage.getId(), aiMessage.getContent(), aiMessage.getCreatedAt(), AI_AGENT);
    }

    @Override
    public List<ChatMessageResponse> getChatHistory() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity userEntity = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        UserEntity aiUserEntity = userRepository.findUserByEmailIgnoreCase(AI_DOCTOR_EMAIL).orElseThrow(() -> new RuntimeException("AI User not found"));
        
        ChatRoomEntity chatRoom = chatRoomRepository.findChatRoomBetweenUsers(userEntity.getUserId(), aiUserEntity.getUserId()).orElseThrow(() -> new RuntimeException("Chat room not found"));
        
        return chatMessageRepository.findByChatRoom(chatRoom).stream()
                .map(message -> new ChatMessageResponse(
                        message.getId(),
                        message.getContent(),
                        message.getCreatedAt(),
                        message.getSender().getId().equals(userEntity.getId()) ? USER : AI_AGENT
                ))
                .collect(Collectors.toList());
    }
}
