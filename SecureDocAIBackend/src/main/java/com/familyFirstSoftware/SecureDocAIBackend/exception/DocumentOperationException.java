package com.familyFirstSoftware.SecureDocAIBackend.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Custom exception for document-related operations with detailed error information.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentOperationException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus status;
    private final String operation;
    private final String documentId;
    private final Map<String, Object> details;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final LocalDateTime timestamp;

    public DocumentOperationException(String message) {
        this("DOC_OPERATION_ERROR", message, HttpStatus.INTERNAL_SERVER_ERROR, "UNKNOWN", "UNKNOWN", null, null);
    }

    public DocumentOperationException(String message, HttpStatus status) {
        this("DOC_OPERATION_ERROR", message, status, "UNKNOWN", "UNKNOWN", null, null);
    }

    public DocumentOperationException(String message, HttpStatus status, Throwable cause) {
        this("DOC_OPERATION_ERROR", message, status, "UNKNOWN", "UNKNOWN", null, cause);
    }
    
    public DocumentOperationException(String errorCode, String message, HttpStatus status, 
                                    String operation, String documentId, 
                                    Map<String, Object> details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode != null ? errorCode : "DOC_OPERATION_ERROR";
        this.status = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
        this.operation = operation != null ? operation : "UNKNOWN";
        this.documentId = documentId != null ? documentId : "UNKNOWN";
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
    
    // Builder pattern for better exception construction
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String errorCode = "DOC_OPERATION_ERROR";
        private String message;
        private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        private String operation = "UNKNOWN";
        private String documentId = "UNKNOWN";
        private Map<String, Object> details;
        private Throwable cause;
        
        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder operation(String operation) {
            this.operation = operation;
            return this;
        }
        
        public Builder documentId(String documentId) {
            this.documentId = documentId;
            return this;
        }
        
        public Builder details(Map<String, Object> details) {
            this.details = details;
            return this;
        }
        
        public Builder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }
        
        public DocumentOperationException build() {
            return new DocumentOperationException(
                errorCode, message, status, operation, documentId, details, cause
            );
        }
    }
    
    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (message == null && getCause() != null) {
            message = getCause().getMessage();
        }
        return message != null ? message : "An error occurred during document operation";
    }
    
    public Map<String, Object> toErrorResponse() {
        return Map.of(
            "timestamp", timestamp,
            "status", status.value(),
            "error", status.getReasonPhrase(),
            "code", errorCode,
            "message", getMessage(),
            "operation", operation,
            "documentId", documentId,
            "details", details != null ? details : Map.of()
        );
    }
}
