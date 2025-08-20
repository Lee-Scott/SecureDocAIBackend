package com.familyFirstSoftware.SecureDocAIBackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for DocumentVersion entity.
 * Used to transfer version information between layers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVersionDto {
    private Long id;
    private String documentId;
    private String filePath;
    private String versionNumber;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    private Long createdBy;
    private Long size;
    private String mimeType;
    private String checksum;
    private String changeDescription;
    
    // Additional metadata that might be useful
    private String createdByName;
    private String createdByEmail;
}
