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
    public static final String FILE_STORAGE = System.getProperty("user.home") + "/Downloads/uploads/";

    public static final String[] PUBLIC_URLS = {
            "/user/logout/**",
            "/user/resetpassword/reset/**",
            "/user/verify/resetpassword/**",
            "/user/resetpassword/**",
            "/user/verify/qrcode/**",
            "/user/login/**",
            "/user/verify/account/**",
            "/user/register/**",
            "/user/new/password/**",
            "/user/verify/**",
            "/user/resetpassword/**",
            "/user/image/**",
            "/user/verify/password/**"
    };

    public static final int NINETY_DAYS = 90;
    public static final int STRENGTH = 12;
    public static final String BASE_PATH = "/**";
    public static final String FILE_NAME = "File-Name";
    public static final String LOGIN_PATH = "/user/login";

    public static final String[] PUBLIC_ROUTES = {
            "/user/logout",
            "/user/resetpassword/reset",
            "/user/verify/resetpassword",
            "/user/resetpassword",
            "/user/verify/qrcode",
            "/user/stream",
            "/user/id",
            "/user/login",
            "/user/register",
            "/user/new/password",
            "/user/verify",
            "/user/refresh/token",
            "/user/resetpassword",
            "/user/image",
            "/user/verify/account",
            "/user/verify/password",
            "/user/verify/code",
            "/user/verify"
    };

    public static final String AUTHORITIES = "authorities";
    public static final String FAMILY_FIRST_SOFTWARE = "FAMILY_FIRST_SOFTWARE";
    public static final String EMPTY_VALUE = "empty";
    public static final String ROLE = "role";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String AUTHORITY_DELIMITER = ",";

    public static final String USER_AUTHORITIES =
            "document:create,document:read,document:update,document:delete";

    public static final String ADMIN_AUTHORITIES =
            "user:create,user:read,user:update,document:create,document:read,document:update,document:delete";

    public static final String SUPER_ADMIN_AUTHORITIES =
            "user:create,user:read,user:update,user:delete,document:create,document:read,document:update,document:delete";

    public static final String MANAGER_AUTHORITIES =
            "document:create,document:read,document:update,document:delete";

    // Query
    public static final String SELECT_DOCUMENTS_QUERY =
            "SELECT doc.id, doc.document_id, doc.name, doc.description, doc.uri, doc.icon, doc.size, doc.formatted_size, " +
                    "doc.extension, doc.reference_id, doc.created_at, doc.updated_at, " +
                    "CONCAT(owner.first_name, ' ', owner.last_name) AS owner_name, owner.email AS owner_email, " +
                    "owner.phone AS owner_phone, owner.last_login AS owner_last_login, " +
                    "CONCAT(updater.first_name, ' ', updater.last_name) AS updater_name " +
                    "FROM documents doc JOIN users owner ON owner.id = doc.created_by " +
                    "JOIN users updater ON updater.id = doc.updated_by";

    public static final String SELECT_DOCUMENT_QUERY =
            SELECT_DOCUMENTS_QUERY + " WHERE doc.document_id = ?1";

    public static final String SELECT_COUNT_DOCUMENTS_QUERY =
            "SELECT COUNT(*) FROM documents";

    public static final String SELECT_DOCUMENTS_BY_NAME_QUERY =
            SELECT_DOCUMENTS_QUERY +
                    " WHERE doc.name ~* :documentName";

    public static final String SELECT_COUNT_DOCUMENTS_BY_NAME_QUERY =
            "SELECT COUNT(*) FROM documents WHERE name ~* :documentName";

    public static final String SYSTEM_GMAIL = "system@gmail.com";
}