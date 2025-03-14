package com.familyFirstSoftware.SecureDocAIBackend.utils;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/10/2024
 *
 *
 */

public class EmailUtils {

    public static String  getEmailMessage(String name, String host, String key) {
        return "Hello " + name + "\n\n" + "Please click the link below to verify your email address:\n\n" +
                getVerificationLink(host, key) + "\n\n Family First Software";
    }

    public static String getResetPasswordMessage(String name, String host, String key) {
        return "Hello " + name + "\n\n" + "Please click the link below to reset your password:\n\n" +
                getResetPasswordUrl(host, key) + "\n\n Family First Software";
    }

    private static String getVerificationLink(String host, String key) {
        return host + "/user/verify?key=" + key;
    }
    private static String getResetPasswordUrl(String host, String key) {
        return host + "/user/verify/password?key=" + key;
    }
}

