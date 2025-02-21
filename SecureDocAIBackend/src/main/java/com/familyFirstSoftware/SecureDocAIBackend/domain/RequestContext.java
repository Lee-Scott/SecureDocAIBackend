package com.familyFirstSoftware.SecureDocAIBackend.domain;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/6/2024
 */

public class RequestContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    private RequestContext() {}

    public static void start() {
        USER_ID.remove();
    }

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }
}