package com.familyFirstSoftware.SecureDocAIBackend.dto;

import com.familyFirstSoftware.SecureDocAIBackend.dto.api.IDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDetailsDto {
    private IDocument document;
    private List<DocumentVersionDto> versions;
    private Map<String, Object> status;
    private String downloadUrl;
    private Map<String, String> versionDownloadUrls;
}
