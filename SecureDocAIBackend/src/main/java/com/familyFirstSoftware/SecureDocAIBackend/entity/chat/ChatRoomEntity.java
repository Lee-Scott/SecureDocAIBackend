package com.familyFirstSoftware.SecureDocAIBackend.entity.chat;

import com.familyFirstSoftware.SecureDocAIBackend.entity.Auditable;
import com.familyFirstSoftware.SecureDocAIBackend.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 7/30/2025
 */

@Entity
@Table(name = "chat_rooms")
@Getter
@Setter
public class ChatRoomEntity extends Auditable {

    @Column(name = "chat_room_id", unique = true, nullable = false, updatable = false)
    private String chatRoomId;

    @Column(name = "reference_id", nullable = false, updatable = false)
    private String referenceId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user1_id", nullable = false)
    private UserEntity user1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user2_id", nullable = false)
    private UserEntity user2;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @PrePersist
    private void generateUuids() {
        if (this.chatRoomId == null) {
            this.chatRoomId = java.util.UUID.randomUUID().toString();
        }
        if (this.referenceId == null) {
            this.referenceId = java.util.UUID.randomUUID().toString();
        }
    }
}