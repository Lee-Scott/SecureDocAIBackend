package com.familyFirstSoftware.SecureDocAIBackend.constant;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 8/2/2025
 *
 * This class isnt even used but for some reason is needed to compile the project because of Maven dependencies.... idk its weird.
 */

public class Queries {
    public static final String GET_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";

    public static final String GET_USER_BY_ID = "SELECT * FROM users WHERE id = ?";

    public static final String GET_USER_BY_USERNAME = "SELECT * FROM users WHERE username = ?";

    public static final String GET_ALL_USERS = "SELECT * FROM users";

    public static final String INSERT_USER = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";

    public static final String UPDATE_USER_PASSWORD = "UPDATE users SET password = ? WHERE id = ?";

    public static final String DELETE_USER_BY_ID = "DELETE FROM users WHERE id = ?";
}

