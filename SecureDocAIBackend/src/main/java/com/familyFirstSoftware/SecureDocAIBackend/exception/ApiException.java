package com.familyFirstSoftware.SecureDocAIBackend.exception;

/**
 * @author Lee Scott
 * @version 1.0
 * @license Family-First Software, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/5/2024
 */

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final HttpStatus status;

    public ApiException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public ApiException() {
        super("An error occurred");
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ApiException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
