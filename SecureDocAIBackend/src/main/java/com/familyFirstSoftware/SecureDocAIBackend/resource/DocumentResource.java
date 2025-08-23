package com.familyFirstSoftware.SecureDocAIBackend.resource;

import com.familyFirstSoftware.SecureDocAIBackend.domain.Response;
import com.familyFirstSoftware.SecureDocAIBackend.dto.Document;
import com.familyFirstSoftware.SecureDocAIBackend.dto.DocumentVersionDto;
import com.familyFirstSoftware.SecureDocAIBackend.dto.User;
import com.familyFirstSoftware.SecureDocAIBackend.dto.api.IDocument;
import com.familyFirstSoftware.SecureDocAIBackend.dtorequest.CheckoutRequest;
import com.familyFirstSoftware.SecureDocAIBackend.dtorequest.PatchDocRequest;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.familyFirstSoftware.SecureDocAIBackend.service.DocumentService;
import com.familyFirstSoftware.SecureDocAIBackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.familyFirstSoftware.SecureDocAIBackend.utils.RequestUtils.getResponse;
import static java.net.URI.create;
import static java.util.Map.of;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.*;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 3/14/2025
 *
 * TODO: Delete Document
 */


@Slf4j
@RestController
@RequestMapping(path = {"/documents"})
@RequiredArgsConstructor
public class DocumentResource {
    private final DocumentService documentService;

    private final UserService userService;

    @PreAuthorize("hasAnyAuthority('document:create') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    @PostMapping("/uploadmultiple")
    //@PreAuthorize("hasAnyAuthority('document:create') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> saveDocuments(@AuthenticationPrincipal User user, @RequestParam("files") List<MultipartFile> documents, HttpServletRequest request) {
        var newDocuments = documentService.saveDocuments(user.getUserId(), documents);
        return ResponseEntity.created(create("")).body(getResponse(request, of("documents", newDocuments), "Document(s) uploaded", CREATED));
    }

    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    @GetMapping
    //@PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getDocuments(@AuthenticationPrincipal User user, HttpServletRequest request,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "5") int size) {
        var documents = documentService.getDocuments(page, size);
        return ResponseEntity.ok(getResponse(request, of("documents", documents), "Document(s) retrieved", OK));
    }

    /**
     * Aggregated document details for frontend convenience (document + versions + status + URLs).
     */
    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    @GetMapping("/{id}/details")
    public ResponseEntity<Response> getDocumentDetails(
            @AuthenticationPrincipal User user,
            @PathVariable("id") String id,
            HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() +
                ((request.getServerPort() == 80 || request.getServerPort() == 443) ? "" : (":" + request.getServerPort()));
        var details = documentService.getDocumentDetails(id, baseUrl);
        return ResponseEntity.ok(getResponse(request, of("details", details), "Document details retrieved", OK));
    }

    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<Response> uploadDocuments(@RequestParam("files") List<MultipartFile> files,
                                                    @RequestParam("userId") String userId,
                                                    HttpServletRequest request) {
        log.info("Uploading documents for user: {}", userId);
        return ResponseEntity.ok(
                getResponse(
                        request,
                        of("documents", documentService.saveDocuments(userId, files)),
                        "Documents uploaded successfully",
                        CREATED
                )
        );
    }
    
    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<Response> searchDocuments(@AuthenticationPrincipal User user, HttpServletRequest request,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "5") int size,
                                                    @RequestParam(value = "name", defaultValue = "") String name) {
        var documents = documentService.getDocuments(page, size, name);
        return ResponseEntity.ok(getResponse(request, of("documents", documents), "Document(s) retrieved", OK));
    }
    
    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    @PostMapping("/review")
    public ResponseEntity<Response> reviewDocuments(
            @AuthenticationPrincipal User user,
            @RequestParam("files") List<MultipartFile> documents,
            @RequestParam(value = "analysisType", required = false, defaultValue = "summary") String analysisType,
            HttpServletRequest request) {
        
        Map<String, Object> reviewResults = documentService.reviewDocuments(
            user.getUserId(), 
            documents, 
            analysisType
        );
        
        return ResponseEntity.ok(
            getResponse(
                request,
                of("reviewResults", reviewResults),
                "Document(s) reviewed successfully",
                OK
            )
        );
    }

    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    @GetMapping("/{documentId}")
    public ResponseEntity<Response> getDocument(@AuthenticationPrincipal User user, @PathVariable("documentId") String documentId, HttpServletRequest request) {
        var document = documentService.getDocumentByDocumentId(documentId);
        return ResponseEntity.ok(getResponse(request, of("document", document), "Document retrieved", OK));
    }

    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    @GetMapping("/by-reference/{referenceId}")
    public ResponseEntity<Response> getDocumentByReferenceId(
            @AuthenticationPrincipal User user,
            @PathVariable("referenceId") String referenceId,
            HttpServletRequest request) {
        var document = documentService.getDocumentByReferenceId(referenceId);
        return ResponseEntity.ok(getResponse(request, of("document", document), "Document retrieved by referenceId", OK));
    }

    /**
     * Resolve by either documentId or referenceId and return the canonical document.
     */
    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    @GetMapping("/resolve/{value}")
    public ResponseEntity<Response> resolveDocument(
            @AuthenticationPrincipal User user,
            @PathVariable("value") String value,
            HttpServletRequest request) {
        try {
            IDocument doc = documentService.resolveDocument(value);
            return ResponseEntity.ok(getResponse(request, of("document", doc), "Document resolved", OK));
        } catch (ApiException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(getResponse(request, of("value", value), e.getMessage(), NOT_FOUND));
        }
    }

    /**
     * Lightweight existence check for a document id (or referenceId via fallback).
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> headDocument(@PathVariable("id") String id) {
        return documentService.documentExists(id) ? ResponseEntity.ok().build() : ResponseEntity.status(NOT_FOUND).build();
    }

    @PreAuthorize("hasAnyAuthority('document:update') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> updateDocument(@AuthenticationPrincipal User user,
                                                   @PathVariable("id") String id,
                                                   @RequestBody PatchDocRequest document,
                                                   HttpServletRequest request) {
        var updatedDocument = documentService.patchDocument(id, document.getName(), document.getDescription());
        return ResponseEntity.ok(getResponse(request, of("document", updatedDocument), "Document updated", OK));
    }

    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    @GetMapping(value = "/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(
            @AuthenticationPrincipal User user, 
            @PathVariable("documentId") String documentId,
            @RequestParam(required = false) String version) throws IOException {
        
        Resource resource = documentService.getResource(documentId, version);
        String filename = resource.getFilename();
        String mimeType = Files.probeContentType(Paths.get(filename));
        
        var httpHeaders = new HttpHeaders();
        httpHeaders.add("File-Name", filename);
        httpHeaders.add(CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        
        httpHeaders.add("Document-Id", documentId);
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .contentType(MediaType.parseMediaType(mimeType))
                .body(resource);
    }
    
    /**
     * Checks in a document by uploading a new version and releasing the lock.
     * Only the user who checked out the document can check it in.
     * 
     * @param documentId The ID of the document to check in
     * @param file The new version of the document file (required)
     * @param user The authenticated user
     * @param request The HTTP request
     * @return Response with the result of the check-in operation
     */
    @PreAuthorize("hasAnyAuthority('document:update') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    @PutMapping(value = "/{documentId}/checkin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> checkinDocument(
            @PathVariable("documentId") String documentId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user,
            HttpServletRequest request) {
        
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(getResponse(request, of(), "File is required", BAD_REQUEST));
        }
        
        try {
            // First, check in the document to release the lock
            Map<String, Object> checkinResult = documentService.checkinDocument(documentId, user.getUserId());
            
            // Then, save the new version of the document
            if (Boolean.TRUE.equals(checkinResult.get("success"))) {
                List<MultipartFile> files = List.of(file);
                Collection<Document> updatedDocuments = documentService.saveDocuments(user.getUserId(), files);
                
                // Update the document version information
                if (!updatedDocuments.isEmpty()) {
                    Document updatedDoc = updatedDocuments.iterator().next();
                    checkinResult.put("document", updatedDoc);
                    checkinResult.put("message", "Document checked in and new version saved successfully");
                }
            }
            
            return ResponseEntity.ok(
                getResponse(request, checkinResult, 
                    (String) checkinResult.getOrDefault("message", "Document checked in"), 
                    OK)
            );
        } catch (Exception e) {
            log.error("Error during document check-in: {}", e.getMessage(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(getResponse(request, of("error", e.getMessage()), 
                    "Failed to check in document", INTERNAL_SERVER_ERROR));
        }
    }

    @PreAuthorize("hasAnyAuthority('document:delete') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    @DeleteMapping("/delete/{documentId}")
    public ResponseEntity<Response> deleteDocument(@AuthenticationPrincipal User user,
                                                   @PathVariable("documentId") String documentId,
                                                   HttpServletRequest request) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.ok(getResponse(request, of(), "Document deleted successfully", OK));
    }
    
    @PreAuthorize("hasAnyAuthority('document:update') or hasAnyRole('USER','DOCTOR', 'SUPER_ADMIN')")
    @PostMapping(value = "/{documentId}/reattach", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> reattachDocument(
            @AuthenticationPrincipal User user,
            @PathVariable("documentId") String documentId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        
        log.info("Reattaching file to document: {}", documentId);
        Document reattachedDoc = documentService.reattachDocument(documentId, file);
        
        return ResponseEntity.ok(
            getResponse(
                request,
                of("document", reattachedDoc),
                "Document file reattached successfully",
                OK
            )
        );
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<Response> checkoutDocument(@PathVariable("id") String documentId, @RequestBody CheckoutRequest checkoutRequest, HttpServletRequest request) {
        Object result = documentService.checkoutDocument(documentId, checkoutRequest.getUserId());
        if (result instanceof java.util.Map && ((java.util.Map<?, ?>) result).get("lockStatus").equals("unavailable")) {
            return ResponseEntity.status(CONFLICT).body(getResponse(request, of("lockInfo", result), "Document is locked by another user", CONFLICT));
        }
        return ResponseEntity.ok(getResponse(request, of("lockInfo", result), "Document lock acquired", OK));
    }
    
    /**
     * Refreshes the document lock for the specified document.
     * Only the user who owns the lock can refresh it.
     * 
     * @param documentId The ID of the document to refresh the lock for
     * @param request The HTTP request
     * @return 200 OK with lock information if successful, 404 if document or lock not found, 409 if lock is owned by another user
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/status")
    public ResponseEntity<Response> getDocumentStatus(
            @PathVariable("id") String documentId,
            HttpServletRequest request) {
        
        Map<String, Object> status = documentService.getDocumentStatus(documentId);
        String statusStr = (String) status.get("status");
        
        if ("not_found".equals(statusStr)) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(getResponse(request, status, (String) status.get("message"), NOT_FOUND));
        }
        
        return ResponseEntity.ok(getResponse(request, status, "Document status retrieved", OK));
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/versions")
    public ResponseEntity<Response> getDocumentVersions(
            @PathVariable("id") String documentId,
            HttpServletRequest request) {
        
        try {
            List<DocumentVersionDto> versions = documentService.getDocumentVersions(documentId);
            return ResponseEntity.ok(getResponse(request, of("versions", versions), "Document versions retrieved", OK));
        } catch (ApiException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(getResponse(request, of("documentId", documentId), e.getMessage(), NOT_FOUND));
        }
    }
    
    @PreAuthorize("hasAnyAuthority('document:read') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    @GetMapping("/{documentId}/versions/{version}/download")
    public ResponseEntity<Resource> downloadVersion(
            @AuthenticationPrincipal User user,
            @PathVariable("documentId") String documentId,
            @PathVariable("version") String version) throws IOException {
        
        return downloadDocument(user, documentId, version);
    }
    
    @PreAuthorize("hasAnyAuthority('document:update') or hasAnyRole('DOCTOR', 'SUPER_ADMIN')")
    @PatchMapping("/{id}/heartbeat")
    public ResponseEntity<Response> refreshDocumentLock(
            @PathVariable("id") String documentId, 
            @AuthenticationPrincipal User user, 
            HttpServletRequest request) {
        try {
            Map<String, Object> result = documentService.refreshDocumentLock(documentId, user.getUserId());
            return ResponseEntity.ok(getResponse(request, of("lockInfo", result), "Document lock refreshed", OK));
        } catch (ApiException e) {
            if (e.getMessage().contains("not found") || e.getMessage().contains("No active lock")) {
                return ResponseEntity.status(NOT_FOUND)
                    .body(getResponse(request, of(), e.getMessage(), NOT_FOUND));
            } else if (e.getMessage().contains("locked by another user") || e.getMessage().contains("expired")) {
                return ResponseEntity.status(CONFLICT)
                    .body(getResponse(request, of("error", e.getMessage()), e.getMessage(), CONFLICT));
            }
            throw e;
        }
    }
}
