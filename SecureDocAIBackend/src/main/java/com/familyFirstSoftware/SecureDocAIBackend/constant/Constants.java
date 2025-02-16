package com.familyFirstSoftware.SecureDocAIBackend.constant;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/7/2024
 *
 * Todo: some should probably be in a properties file like NINETY_DAYS or other changeable constants
 */

public class Constants {
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ROLE = "ROLE";
    public static final String EMPTY_VALUE = "empty";
    public static final String AUTHORITY_DELIMITER = ",";
    public static final String AUTHORITIES = "authorities";
    public static final String USER_AUTHORITIES = "document:create,document:read,document:update,document:delete";
    public static final String ADMIN_AUTHORITIES = "user:create,user:read,user:update,document:create,document:read,document:update,document:delete";
    public static final String SUPER_ADMIN_AUTHORITIES = "user:create,user:read,user:update,user:delete,document:create,document:read,document:update,document:delete";
    public static final String MANAGER_AUTHORITIES = "document:create,document:read,document:update,document:delete";
    public static final String LOGIN_PATH = "user/login";
    public static final String FAMILY_FIRST_SOFTWARE = "FAMILY_FIRST_SOFTWARE";
    public static final int NINETY_DAYS = 90;
    public static final int STRENGTH = 12;
    //public static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public static final String[] PUBLIC_URLS = { "/user/resetpassword/reset/**", "/user/verify/resetpassword/**", "/user/resetpassword/**", "/user/verify/qrcode/**", "/user/login/**", "/user/verify/account/**", "/user/register/**", "/user/new/password/**", "/user/verify/**", "/user/resetpassword/**", "/user/image/**", "/user/verify/password/**" };
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";

    public static final String TYPE = "typ";
    public static final String JWT_TYPE = "JWT";

}

