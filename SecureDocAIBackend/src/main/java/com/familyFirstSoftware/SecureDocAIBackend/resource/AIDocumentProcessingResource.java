package com.familyFirstSoftware.SecureDocAIBackend.resource;

import com.familyFirstSoftware.SecureDocAIBackend.dto.response.HttpResponse;
import com.familyFirstSoftware.SecureDocAIBackend.service.AIReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/ai/reports")
@RequiredArgsConstructor
@Tag(name = "AI Document Processing", description = "APIs for processing and analyzing documents with AI")
public class AIDocumentProcessingResource {

    private final AIReportService aiReportService;

    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Process a single document for AI analysis",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Document processed successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid file or parameters"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
            })
    public CompletableFuture<ResponseEntity<HttpResponse>> processDocument(
            @RequestParam("file") MultipartFile file) {
        return aiReportService.analyzeUploadedDocument(file, "default")
                .thenApply(analysis -> ResponseEntity.ok(
                        HttpResponse.ok(analysis, "Document processed successfully")
                ));
    }

    @PostMapping("/process/batch")
    @Operation(summary = "Process multiple documents in a batch for AI analysis",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Documents processed successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid files or parameters"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
            })
    public CompletableFuture<ResponseEntity<HttpResponse>> processDocumentsBatch(
            @RequestParam("files") MultipartFile[] files) {
        // Implementation for batch processing
        return CompletableFuture.completedFuture(
                ResponseEntity.ok(HttpResponse.ok(null, "Batch processing not yet implemented"))
        );
    }

    @PostMapping(value = "/process-and-send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Process a single document and send the result to a chat room",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Document processed and sent successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid file or parameters"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
            })
    public CompletableFuture<ResponseEntity<HttpResponse>> processAndSendToChat(
            @RequestParam("file") MultipartFile file,
            @RequestParam("chatRoomId") String chatRoomId) {
        // Implementation for processing and sending to chat
        return CompletableFuture.completedFuture(
                ResponseEntity.ok(HttpResponse.ok(null, "Process and send not yet implemented"))
        );
    }

    @GetMapping("/limits")
    @Operation(summary = "Get the document processing limits",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Limits retrieved successfully",
                            content = @Content(schema = @Schema(implementation = Map.class)))
            })
    public ResponseEntity<HttpResponse> getProcessingLimits() {
        // Return default limits
        return ResponseEntity.ok(HttpResponse.ok(
                Map.of(
                        "maxFileSize", "10MB",
                        "allowedTypes", new String[]{"pdf", "docx", "txt"},
                        "maxConcurrentProcesses", 5
                ),
                "Processing limits retrieved"
        ));
    }
}
