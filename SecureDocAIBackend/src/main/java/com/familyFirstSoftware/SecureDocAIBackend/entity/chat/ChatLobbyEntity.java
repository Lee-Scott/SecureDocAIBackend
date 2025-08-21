package com.familyFirstSoftware.SecureDocAIBackend.entity.chat;

import com.familyFirstSoftware.SecureDocAIBackend.entity.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 7/30/2025
 */

// ChatLobbyEntity.java
@Entity
@Table(name = "chat_lobbies")
@Getter
@Setter
public class ChatLobbyEntity extends Auditable {

    @Column(name = "lobby_id", unique = true, nullable = false, updatable = false)
    @UuidGenerator
    private String lobbyId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_participants")
    private Integer maxParticipants = 50;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "lobby", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LobbyParticipantEntity> participants = new HashSet<>();
}
