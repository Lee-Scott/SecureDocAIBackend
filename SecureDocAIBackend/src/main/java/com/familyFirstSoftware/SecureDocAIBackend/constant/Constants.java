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
            "document:create,document:read,document:update,document:delete,chat:read,chat:write,lobby:read";

    public static final String ADMIN_AUTHORITIES =
            "user:create,user:read,user:update,document:create,document:read,document:update,document:delete,chat:read,chat:write,lobby:read";

    public static final String SUPER_ADMIN_AUTHORITIES =
            "user:create,user:read,user:update,user:delete,document:create,document:read,document:update,document:delete,chat:read,chat:write,chat:delete,lobby:create,lobby:read,lobby:update,lobby:delete,lobby:manage";

    public static final String MANAGER_AUTHORITIES =
            "document:create,document:read,document:update,document:delete,chat:read,chat:write,lobby:create,lobby:read,lobby:update,lobby:manage";

    public static final String AI_AUTHORITIES =
            "chat:read,chat:write,document:read,lobby:read";

    public static final String SYSTEM_GMAIL = "system@gmail.com";

    // ===== DOCUMENT QUERIES =====
    public static final String DOCUMENT_SELECT_ALL_QUERY =
            "SELECT doc.id, doc.document_id, doc.name, doc.description, doc.uri, doc.icon, doc.size, doc.formatted_size, " +
                    "doc.extension, doc.reference_id, doc.created_at, doc.updated_at, " +
                    "CONCAT(owner.first_name, ' ', owner.last_name) AS owner_name, owner.email AS owner_email, " +
                    "owner.phone AS owner_phone, owner.last_login AS owner_last_login, " +
                    "CONCAT(updater.first_name, ' ', updater.last_name) AS updater_name " +
                    "FROM documents doc JOIN users owner ON owner.id = doc.created_by " +
                    "JOIN users updater ON updater.id = doc.updated_by";

    public static final String DOCUMENT_SELECT_BY_ID_QUERY =
            DOCUMENT_SELECT_ALL_QUERY + " WHERE doc.document_id = ?1";

    public static final String DOCUMENT_COUNT_ALL_QUERY =
            "SELECT COUNT(*) FROM documents";

    public static final String DOCUMENT_SELECT_BY_NAME_QUERY =
            DOCUMENT_SELECT_ALL_QUERY + " WHERE doc.name ~* :documentName";

    public static final String DOCUMENT_COUNT_BY_NAME_QUERY =
            "SELECT COUNT(*) FROM documents WHERE name ~* :documentName";

    // ===== CHAT ROOM QUERIES =====
    public static final String CHATROOM_FIND_BETWEEN_USERS_QUERY =
            "SELECT cr FROM ChatRoomEntity cr WHERE " +
            "(cr.user1.userId = :user1Id AND cr.user2.userId = :user2Id) OR " +
            "(cr.user1.userId = :user2Id AND cr.user2.userId = :user1Id)";

    public static final String CHATROOM_FIND_ACTIVE_FOR_USER_QUERY =
            "SELECT cr FROM ChatRoomEntity cr WHERE " +
            "(cr.user1.userId = :userId OR cr.user2.userId = :userId) AND cr.isActive = true";

    public static final String CHATROOM_EXISTS_BY_USER_QUERY =
            "SELECT COUNT(cr) > 0 FROM ChatRoomEntity cr WHERE " +
            "cr.user1.userId = :userId OR cr.user2.userId = :userId";

    public static final String CHATROOM_FIND_ALL_FOR_USER_QUERY =
            "SELECT cr FROM ChatRoomEntity cr WHERE " +
            "cr.user1.userId = :userId OR cr.user2.userId = :userId";

    // ===== CHAT MESSAGE QUERIES =====
    public static final String CHATMESSAGE_SELECT_BY_ROOM_ID_QUERY =
            "SELECT m FROM ChatMessageEntity m " +
            "JOIN FETCH m.sender s " +
            "JOIN FETCH s.role " +
            "WHERE m.chatRoom.chatRoomId = :chatRoomId " +
            "ORDER BY m.createdAt ASC";

    // ===== QUESTIONNAIRE QUERIES =====
    public static final String QUESTIONNAIRE_FIND_WITH_FILTERS_QUERY =
            "SELECT q FROM QuestionnaireEntity q WHERE q.isActive = true AND " +
            "(:category IS NULL OR q.category = :category)";

    public static final String QUESTIONNAIRE_COUNT_BY_CREATED_BY_QUERY =
            "SELECT COUNT(q) FROM QuestionnaireEntity q WHERE q.createdBy = :userId";

    // ===== QUESTIONNAIRE RESPONSE QUERIES =====
    public static final String QUESTIONNAIRE_RESPONSE_FIND_BY_QUESTIONNAIRE_ID_QUERY =
            "SELECT qr FROM QuestionnaireResponseEntity qr WHERE qr.questionnaire.id = :questionnaireId";

    public static final String QUESTIONNAIRE_RESPONSE_COUNT_BY_QUESTIONNAIRE_ID_QUERY =
            "SELECT COUNT(qr) FROM QuestionnaireResponseEntity qr WHERE qr.questionnaire.id = :questionnaireId";

    public static final String QUESTIONNAIRE_RESPONSE_COUNT_COMPLETED_BY_QUESTIONNAIRE_ID_QUERY =
            "SELECT COUNT(qr) FROM QuestionnaireResponseEntity qr WHERE qr.questionnaire.id = :questionnaireId AND qr.isCompleted = true";

    public static final String QUESTIONNAIRE_RESPONSE_AVG_COMPLETION_TIME_QUERY =
            "SELECT AVG((EXTRACT(EPOCH FROM qr.completed_at) - EXTRACT(EPOCH FROM qr.started_at))/60.0) " +
            "FROM questionnaire_responses qr " +
            "WHERE qr.questionnaire_id = :questionnaireId AND qr.is_completed = true";

    // ===== QUESTION RESPONSE QUERIES =====
    public static final String QUESTION_RESPONSE_FIND_BY_QUESTION_ID_QUERY =
            "SELECT qr FROM QuestionResponseEntity qr WHERE qr.question.id = :questionId";

    public static final String QUESTION_RESPONSE_COUNT_BY_QUESTION_ID_QUERY =
            "SELECT COUNT(qr) FROM QuestionResponseEntity qr WHERE qr.question.id = :questionId";

    public static final String QUESTION_RESPONSE_COUNT_SKIPPED_BY_QUESTION_ID_QUERY =
            "SELECT COUNT(qr) FROM QuestionResponseEntity qr WHERE qr.question.id = :questionId AND qr.isSkipped = true";

    public static final String QUESTION_RESPONSE_ANSWER_DISTRIBUTION_QUERY =
            "SELECT qr.answerValue, COUNT(qr) FROM QuestionResponseEntity qr " +
            "WHERE qr.question.id = :questionId AND qr.isSkipped = false " +
            "GROUP BY qr.answerValue ORDER BY COUNT(qr) DESC";
}
