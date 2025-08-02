package com.familyFirstSoftware.SecureDocAIBackend.repository;

import com.familyFirstSoftware.SecureDocAIBackend.entity.chat.ChatMessageEntity;
import com.familyFirstSoftware.SecureDocAIBackend.entity.chat.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.CHATMESSAGE_SELECT_BY_ROOM_ID_QUERY;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByChatRoom(ChatRoomEntity chatRoom);

    @Query(CHATMESSAGE_SELECT_BY_ROOM_ID_QUERY)
    List<ChatMessageEntity> findMessagesByChatRoomId(@Param("chatRoomId") String chatRoomId);
}