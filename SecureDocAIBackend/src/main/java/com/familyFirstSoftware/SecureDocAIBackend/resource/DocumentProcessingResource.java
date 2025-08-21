package com.familyFirstSoftware.SecureDocAIBackend.resource;

import com.familyFirstSoftware.SecureDocAIBackend.service.ai.DocumentProcessingService;
import com.familyFirstSoftware.SecureDocAIBackend.service.ChatRoomService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.chat.MessageType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai/documents")
@RequiredArgsConstructor
@Tag(name = "AI Document Processing", description = "APIs for processing and analyzing documents with AI")
public class DocumentProcessingResource {

        private final DocumentProcessingService documentProcessingService;
    private final ChatRoomService chatRoomService;

    @PostMapping(value = "/process", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('document:read')")
    @Operation(summary = "Process a single document for AI analysis",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Document processed successfully",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid file or parameters"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
            })
    public ResponseEntity<String> processDocument(@RequestParam("file") MultipartFile file) {
        String result = documentProcessingService.processDocument(file);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/process/batch", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('document:read')")
    @Operation(summary = "Process multiple documents in a batch for AI analysis",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Documents processed successfully",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid files or parameters"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
            })
    public ResponseEntity<String> processDocuments(@RequestParam("files") List<MultipartFile> files) {
        String result = documentProcessingService.processDocuments(files);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/limits")
    @Operation(summary = "Get the document processing limits",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Limits retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
            })
    public ResponseEntity<Map<String, Object>> getLimits() {
        return ResponseEntity.ok(documentProcessingService.getProcessingLimits());
    }
    
    @PostMapping(value = "/process-and-send", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('document:read')")
    @Operation(summary = "Process a single document and send the result to a chat room",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Document processed and sent successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid file or parameters"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
            })
    public ResponseEntity<Void> processDocumentAndSend(
            @RequestParam("file") MultipartFile file,
            @RequestParam("chatRoomId") String chatRoomId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.familyFirstSoftware.SecureDocAIBackend.dto.User userPrincipal) {
        String result = documentProcessingService.processDocument(file);
        chatRoomService.sendMessage(chatRoomId, userPrincipal.getUserId(), result, com.familyFirstSoftware.SecureDocAIBackend.enumeration.chat.MessageType.TEXT);
        return ResponseEntity.ok().build();
    }
}
