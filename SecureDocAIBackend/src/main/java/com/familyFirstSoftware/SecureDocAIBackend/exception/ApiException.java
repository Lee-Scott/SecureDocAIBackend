package com.familyFirstSoftware.SecureDocAIBackend.exception;

/**
 * @author Lee Scott
 * @version 1.0
 * @license Family-First Software, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/5/2024
 */

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
    public ApiException() {
        super("An error occurred");
    }
}

