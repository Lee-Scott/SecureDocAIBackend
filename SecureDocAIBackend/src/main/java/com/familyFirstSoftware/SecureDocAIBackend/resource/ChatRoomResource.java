package com.familyFirstSoftware.SecureDocAIBackend.resource;

import com.familyFirstSoftware.SecureDocAIBackend.domain.Response;
import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.dto.chat.ChatMessage;
import com.familyFirstSoftware.SecureDocAIBackend.dto.chat.ChatRoom;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.chat.MessageType;
import com.familyFirstSoftware.SecureDocAIBackend.service.ChatRoomService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.familyFirstSoftware.SecureDocAIBackend.utils.RequestUtils.getResponse;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 7/30/2025
 */

@Slf4j
@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
public class ChatRoomResource {

    private final ChatRoomService chatRoomService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('chat:write') or hasAnyRole('USER', 'DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> createChatRoom(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        // Extract user IDs from nested objects
        Map<String, String> user1Map = (Map<String, String>) request.get("user1");
        Map<String, String> user2Map = (Map<String, String>) request.get("user2");

        String user1Id = user1Map.get("userId");
        String user2Id = user2Map.get("userId");

        ChatRoom chatRoom = chatRoomService.createChatRoom(user1Id, user2Id);
        return ResponseEntity.status(CREATED).body(getResponse(httpRequest, Map.of("chatRoom", chatRoom), "Chat room created successfully.", CREATED));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('chat:read') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getAllChatRooms(HttpServletRequest request) {
        List<ChatRoom> chatRooms = chatRoomService.getAllChatRooms();
        return ResponseEntity.ok().body(getResponse(request, Map.of("chatRooms", chatRooms), "Chat rooms retrieved successfully.", OK));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('chat:read') or hasAnyRole('USER', 'DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getChatRoomsForUser(@PathVariable String userId, HttpServletRequest request) {
        List<ChatRoom> chatRooms = chatRoomService.getChatRoomsForUser(userId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("chatRooms", chatRooms), "User chat rooms retrieved successfully.", OK));
    }

    @GetMapping("/{chatRoomId}")
    @PreAuthorize("hasAnyAuthority('chat:read') or hasAnyRole('USER', 'DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getChatRoom(@PathVariable String chatRoomId, HttpServletRequest request) {
        Optional<ChatRoom> chatRoom = chatRoomService.getChatRoomById(chatRoomId);
        if (chatRoom.isPresent()) {
            return ResponseEntity.ok().body(getResponse(request, Map.of("chatRoom", chatRoom.get()), "Chat room retrieved successfully.", OK));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{chatRoomId}/messages")
    @PreAuthorize("hasAnyAuthority('chat:write') or hasAnyRole('USER', 'DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> sendMessageToChatRoom(
            @PathVariable String chatRoomId,
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest httpRequest
    ) {
        String content = (String) request.get("content");
        String messageTypeStr = (String) request.getOrDefault("messageType", "TEXT");
        MessageType messageType = MessageType.valueOf(messageTypeStr);

        ChatMessage message = chatRoomService.sendMessage(chatRoomId, userPrincipal.getUserId(), content, messageType);
        return ResponseEntity.ok().body(getResponse(httpRequest, Map.of("message", message), "Message sent successfully.", OK));
    }

    @GetMapping("/{chatRoomId}/messages")
    @PreAuthorize("hasAnyAuthority('chat:read') or hasAnyRole('USER', 'DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getMessagesForChatRoom(@PathVariable String chatRoomId, HttpServletRequest request) {
        List<ChatMessage> messages = chatRoomService.getMessagesForChatRoom(chatRoomId);
        return ResponseEntity.ok().body(getResponse(request, Map.of("messages", messages), "Messages retrieved successfully.", OK));
    }

    @GetMapping("/user/healthcare-providers")
    @PreAuthorize("hasAnyAuthority('user:read') or hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getHealthcareProviders(HttpServletRequest request) {
        List<User> providers = chatRoomService.getHealthcareProviders();
        return ResponseEntity.ok().body(getResponse(request, Map.of("providers", providers), "Healthcare providers retrieved successfully.", OK));
    }

    @PostMapping("/with-provider")
    @PreAuthorize("hasAnyAuthority('chat:write') or hasAnyRole('USER', 'DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> createChatRoomWithProvider(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest httpRequest
    ) {
        String providerId = (String) request.get("providerId");
        ChatRoom chatRoom = chatRoomService.createChatRoom(userPrincipal.getUserId(), providerId);
        return ResponseEntity.status(CREATED).body(getResponse(httpRequest, Map.of("chatRoom", chatRoom), "Chat room with provider created successfully.", CREATED));
    }

    @PostMapping("/{chatRoomId}/share-results")
    @PreAuthorize("hasAnyAuthority('chat:write') or hasAnyRole('USER', 'DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> shareQuestionnaireResults(
            @PathVariable String chatRoomId,
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest httpRequest
    ) {
        String questionnaireId = (String) request.get("questionnaireId");
        chatRoomService.shareQuestionnaireResults(chatRoomId, userPrincipal.getUserId(), questionnaireId);
        return ResponseEntity.ok().body(getResponse(httpRequest, null, "Questionnaire results shared successfully.", OK));
    }
}