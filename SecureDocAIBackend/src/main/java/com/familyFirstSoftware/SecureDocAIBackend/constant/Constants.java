package com.familyFirstSoftware.SecureDocAIBackend.constant;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/7/2024
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

    public static final String FAMILY_FIRST_SOFTWARE = "FAMILY_FIRST_SOFTWARE";

    public static final String TYPE = "typ";
    public static final String JWT_TYPE = "JWT";

}

