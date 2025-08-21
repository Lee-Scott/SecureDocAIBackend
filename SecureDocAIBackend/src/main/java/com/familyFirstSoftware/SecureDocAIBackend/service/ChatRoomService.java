package com.familyFirstSoftware.SecureDocAIBackend.service;

import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.dto.chat.ChatMessage;
import com.familyFirstSoftware.SecureDocAIBackend.dto.chat.ChatRoom;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.chat.MessageType;

import java.util.List;
import java.util.Optional;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/1/2025
 *
 * This interface defines the operations related to chat room management.
 */
public interface ChatRoomService {

    ChatRoom createChatRoom(String user1Id, String user2Id);

    List<ChatRoom> getAllChatRooms();

    List<ChatRoom> getChatRoomsForUser(String userId);

    Optional<ChatRoom> getChatRoomById(String chatRoomId);

    ChatMessage sendMessage(String chatRoomId, String senderId, String content, MessageType messageType);

    List<ChatMessage> getMessagesForChatRoom(String chatRoomId);

    List<User> getHealthcareProviders();

    void shareQuestionnaireResults(String chatRoomId, String userId, String questionnaireId);
}
