package com.familyFirstSoftware.SecureDocAIBackend.constant;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 3/12/2025
 */

public class Querys {

    public static final String SELECT_DOCUMENTS_QUERY = "SELECT doc.id, doc.document_id, doc.name, doc.description, doc.uri, doc.icon, doc.size, doc.formatted_size, doc.extension, doc.reference_id, doc.created_at, doc.updated_at, CONCAT(owner.first_name, ' ', owner.last_name) AS owner_name, owner.email AS owner_email, owner.phone AS owner_phone, owner.last_login AS owner_last_login, CONCAT(updater.first_name, ' ', updater.last_name) AS updater_name FROM documents doc JOIN users owner ON owner.id = doc.created_by JOIN users updater ON updater.id = doc.updated_by";
    public static final String SELECT_COUNT_DOCUMENTS_QUERY = "SELECT COUNT(*) FROM documents";
}

