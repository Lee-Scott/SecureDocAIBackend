package com.familyFirstSoftware.SecureDocAIBackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DocumentVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String documentId;
    private String filePath;
    private String versionNumber;
    private Long size;
    private String mimeType;
    private String checksum;
    private Long createdBy;
    private String changeDescription;

    @CreatedDate
    private LocalDateTime createdAt;
}
