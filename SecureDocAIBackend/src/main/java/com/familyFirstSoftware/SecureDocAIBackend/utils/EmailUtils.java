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

    public static String  getEmailMessage(String name, String host, String token) {
        return "Hello " + name + "\n\n" + "Please click the link below to verify your email address:\n\n" +
                getVerificationLink(host, token) + "\n\nFamilyFirstSoftware";
    }

    public static String getResetPasswordMessage(String name, String host, String token) {
        return "Hello " + name + "\n\n" + "Please click the link below to verify your email address:\n\n" +
                getResetPasswordUrl(host, token) + "\n\nFamilyFirstSoftware";
    }

    private static String getVerificationLink(String host, String token) {
        return host + "/verify?token=" + token;
    }
    private static String getResetPasswordUrl(String host, String token) {
        return host + "/verify/password?token=" + token;
    }
}

