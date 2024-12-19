package com.familyFirstSoftware.SecureDocAIBackend.service;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/10/2024
 */
public interface EmailService {
    void sendNewAccountEmail(String name, String toEmail, String token);
    void sendPasswordResetEmail(String name, String toEmail, String token);

}
