package com.familyFirstSoftware.SecureDocAIBackend.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/12/2024
 *
 */

public interface IDocument {
    Long getId();
    void setId(Long id);
    @JsonProperty("documentId")
    String getDocumentId();
    void setDocumentId(String documentId);
    String getName();
    void setName(String name);
    String getDescription();
    void setDescription(String description);
    String getUri();
    void setUri(String uri);
    String getIcon();
    void setIcon(String icon);
    long getSize();
    void setSize(long size);
    @JsonProperty("formattedSize")
    String getFormattedSize();
    void setFormattedSize(String formattedSize);
    String getExtension();
    void setExtension(String extension);
    @JsonProperty("referenceId")
    String getReferenceId();
    void setReferenceId(String referenceId);
    @JsonProperty("createdAt")
    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);
    @JsonProperty("updatedAt")
    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);
    @JsonProperty("ownerName")
    String getOwnerName();
    void setOwnerName(String ownerName);
    @JsonProperty("ownerEmail")
    String getOwnerEmail();
    void setOwnerEmail(String ownerEmail);
    @JsonProperty("ownerPhone")
    String getOwnerPhone();
    void setOwnerPhone(String ownerPhone);
    @JsonProperty("ownerLastLogin")
    LocalDateTime getOwnerLastLogin();
    void setOwnerLastLogin(LocalDateTime ownerLastLogin);
    @JsonProperty("updaterName")
    String getUpdaterName();
    void setUpdaterName(String updaterName);
}