package com.familyFirstSoftware.SecureDocAIBackend.repository;

import com.familyFirstSoftware.SecureDocAIBackend.entity.chat.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.familyFirstSoftware.SecureDocAIBackend.constant.Constants.*;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 7/30/2025
 */

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    Optional<ChatRoomEntity> findByChatRoomId(String chatRoomId);

    // Find chat room between two specific users (order doesn't matter)
    @Query(CHATROOM_FIND_BETWEEN_USERS_QUERY)
    Optional<ChatRoomEntity> findChatRoomBetweenUsers(@Param("user1Id") String user1Id,
                                                      @Param("user2Id") String user2Id);

    // Find all active chat rooms for a specific user
    @Query(CHATROOM_FIND_ACTIVE_FOR_USER_QUERY)
    List<ChatRoomEntity> findActiveRoomsForUser(@Param("userId") String userId);

    // Check if user has any chat rooms (for deletion checks)
    @Query(CHATROOM_EXISTS_BY_USER_QUERY)
    boolean existsByUser(@Param("userId") String userId);

    // Find all chat rooms for a user (including inactive ones)
    @Query(CHATROOM_FIND_ALL_FOR_USER_QUERY)
    List<ChatRoomEntity> findAllRoomsForUser(@Param("userId") String userId);

    boolean existsByCreatedBy(Long userEntityId);
    boolean existsByUpdatedBy(Long userEntityId);
}
