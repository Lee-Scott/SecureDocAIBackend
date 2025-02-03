package com.familyFirstSoftware.SecureDocAIBackend.enumeration;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 2/3/2025
 */

public enum TokenType {
    ACCESS("access-token"), REFRESH("refresh-token");
    private final String value;

    TokenType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}

