BEGIN;

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    user_id CHARACTER VARYING(255) NOT NULL,
    first_name CHARACTER VARYING(50) NOT NULL,
    last_name CHARACTER VARYING(50) NOT NULL,
    email CHARACTER VARYING(100) NOT NULL,
    phone CHARACTER VARYING(30) DEFAULT NULL,
    bio CHARACTER VARYING(255) DEFAULT NULL,
    reference_id CHARACTER VARYING(255) NOT NULL,
    qr_code_secret CHARACTER VARYING(255) DEFAULT NULL,
    qr_code_image_uri TEXT DEFAULT NULL,
    image_url CHARACTER VARYING(255) DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
    last_login TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    login_attempts INTEGER DEFAULT 0,
    mfa BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN NOT NULL DEFAULT FALSE,
    account_non_locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_user_id UNIQUE (user_id)
);

CREATE TABLE IF NOT EXISTS confirmations (
    id BIGSERIAL PRIMARY KEY,
    key CHARACTER VARYING(255) NOT NULL,
    user_id BIGINT NOT NULL,
    reference_id CHARACTER VARYING(255) NOT NULL,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_confirmations_user_id UNIQUE (user_id),
    CONSTRAINT uq_confirmations_key UNIQUE (key),
    CONSTRAINT fk_confirmations_user_id FOREIGN KEY (user_id) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_confirmations_created_by FOREIGN KEY (created_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_confirmations_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS credentials (
    id BIGSERIAL PRIMARY KEY,
    password CHARACTER VARYING(255) NOT NULL,
    reference_id CHARACTER VARYING(255) NOT NULL,
    user_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_credentials_user_id UNIQUE (user_id),
    CONSTRAINT fk_credentials_user_id FOREIGN KEY (user_id) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_credentials_created_by FOREIGN KEY (created_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_credentials_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS documents (
    id BIGSERIAL PRIMARY KEY,
    document_id CHARACTER VARYING(255) NOT NULL,
    reference_id CHARACTER VARYING(255) NOT NULL,
    extension CHARACTER VARYING(10) NOT NULL,
    formatted_size CHARACTER VARYING(20) NOT NULL,
    icon CHARACTER VARYING(255) NOT NULL,
    name CHARACTER VARYING(50) NOT NULL,
    size BIGINT NOT NULL,
    uri CHARACTER VARYING(255) NOT NULL,
    description CHARACTER VARYING(255),
    user_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_documents_document_id UNIQUE (document_id),
    CONSTRAINT fk_documents_owner FOREIGN KEY (user_id) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_documents_created_by FOREIGN KEY (created_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_documents_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    authorities CHARACTER VARYING(255) NOT NULL,
    name CHARACTER VARYING(255) NOT NULL,
    reference_id CHARACTER VARYING(255) NOT NULL,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_roles_name UNIQUE (name),
    CONSTRAINT fk_roles_created_by FOREIGN KEY (created_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_roles_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS user_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT uq_user_roles_user_role UNIQUE (user_id, role_id),
    CONSTRAINT fk_user_roles_user_id FOREIGN KEY (user_id) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_user_roles_role_id FOREIGN KEY (role_id) REFERENCES roles (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS chat_rooms (
    id BIGSERIAL PRIMARY KEY,
    chat_room_id CHARACTER VARYING(255) NOT NULL,
    reference_id CHARACTER VARYING(255) NOT NULL,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_chat_rooms_id UNIQUE (chat_room_id),
    CONSTRAINT uq_chat_rooms_users UNIQUE (user1_id, user2_id),
    CONSTRAINT fk_chat_rooms_user1_id FOREIGN KEY (user1_id) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_chat_rooms_user2_id FOREIGN KEY (user2_id) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_chat_rooms_created_by FOREIGN KEY (created_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_chat_rooms_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT check_different_users CHECK (user1_id != user2_id)
);

CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGSERIAL PRIMARY KEY,
    message_id CHARACTER VARYING(255) NOT NULL,
    chat_room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    message_type CHARACTER VARYING(20) NOT NULL DEFAULT 'TEXT',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_chat_messages_id UNIQUE (message_id),
    CONSTRAINT fk_chat_messages_chat_room_id FOREIGN KEY (chat_room_id) REFERENCES chat_rooms (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_chat_messages_sender_id FOREIGN KEY (sender_id) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT check_message_type CHECK (message_type IN ('TEXT', 'DOCUMENT', 'SYSTEM'))
);

CREATE TABLE IF NOT EXISTS chat_room_documents (
    id BIGSERIAL PRIMARY KEY,
    chat_room_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    added_by BIGINT NOT NULL,
    added_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_chat_room_documents UNIQUE (chat_room_id, document_id),
    CONSTRAINT fk_chat_room_documents_chat_room_id FOREIGN KEY (chat_room_id) REFERENCES chat_rooms (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_chat_room_documents_document_id FOREIGN KEY (document_id) REFERENCES documents (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_chat_room_documents_added_by FOREIGN KEY (added_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS document_analysis (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL,
    summary TEXT,
    key_points TEXT,
    content_hash CHARACTER VARYING(255) NOT NULL,
    analyzed_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_document_analysis_document_id UNIQUE (document_id),
    CONSTRAINT fk_document_analysis_document_id FOREIGN KEY (document_id) REFERENCES documents (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_lobbies (
    id BIGSERIAL PRIMARY KEY,
    lobby_id CHARACTER VARYING(255) NOT NULL,
    name CHARACTER VARYING(100) NOT NULL,
    description TEXT,
    max_participants INTEGER DEFAULT 50,
    is_public BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_chat_lobbies_id UNIQUE (lobby_id),
    CONSTRAINT fk_chat_lobbies_created_by FOREIGN KEY (created_by) REFERENCES users (id),
    CONSTRAINT fk_chat_lobbies_updated_by FOREIGN KEY (updated_by) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS lobby_participants (
    id BIGSERIAL PRIMARY KEY,
    lobby_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role CHARACTER VARYING(20) DEFAULT 'PARTICIPANT',
    joined_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_lobby_participants UNIQUE (lobby_id, user_id),
    CONSTRAINT fk_lobby_participants_lobby_id FOREIGN KEY (lobby_id) REFERENCES chat_lobbies (id),
    CONSTRAINT fk_lobby_participants_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT check_participant_role CHECK (role IN ('MODERATOR', 'PARTICIPANT'))
);

-- Questionnaire System Tables
CREATE TABLE IF NOT EXISTS questionnaires (
    id BIGSERIAL PRIMARY KEY,
    reference_id CHARACTER VARYING(255) NOT NULL,
    title CHARACTER VARYING(255) NOT NULL,
    description TEXT,
    category CHARACTER VARYING(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    estimated_time_minutes INTEGER NOT NULL,
    created_by_user_id BIGINT,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_questionnaires_reference_id UNIQUE (reference_id),
    CONSTRAINT fk_questionnaires_created_by_user FOREIGN KEY (created_by_user_id) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS question_pages (
    id BIGSERIAL PRIMARY KEY,
    reference_id CHARACTER VARYING(255) NOT NULL,
    questionnaire_id BIGINT NOT NULL,
    page_number INTEGER NOT NULL,
    title CHARACTER VARYING(255) NOT NULL,
    description TEXT,
    is_required BOOLEAN NOT NULL DEFAULT TRUE,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_question_pages_reference_id UNIQUE (reference_id),
    CONSTRAINT uq_question_pages_questionnaire_page UNIQUE (questionnaire_id, page_number),
    CONSTRAINT fk_question_pages_questionnaire FOREIGN KEY (questionnaire_id) REFERENCES questionnaires (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS questions (
    id BIGSERIAL PRIMARY KEY,
    reference_id CHARACTER VARYING(255) NOT NULL,
    page_id BIGINT NOT NULL,
    question_number INTEGER NOT NULL,
    question_text TEXT NOT NULL,
    question_type CHARACTER VARYING(50) NOT NULL,
    is_required BOOLEAN NOT NULL DEFAULT TRUE,
    help_text TEXT,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_questions_reference_id UNIQUE (reference_id),
    CONSTRAINT uq_questions_page_number UNIQUE (page_id, question_number),
    CONSTRAINT fk_questions_page FOREIGN KEY (page_id) REFERENCES question_pages (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS question_validation_rules (
    question_id BIGINT NOT NULL,
    rule_key CHARACTER VARYING(255) NOT NULL,
    rule_value CHARACTER VARYING(255),
    PRIMARY KEY (question_id, rule_key),
    CONSTRAINT fk_validation_rules_question FOREIGN KEY (question_id) REFERENCES questions (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS question_options (
    id BIGSERIAL PRIMARY KEY,
    reference_id CHARACTER VARYING(255) NOT NULL,
    question_id BIGINT NOT NULL,
    option_text CHARACTER VARYING(255) NOT NULL,
    option_value CHARACTER VARYING(255) NOT NULL,
    order_index INTEGER NOT NULL,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_question_options_reference_id UNIQUE (reference_id),
    CONSTRAINT uq_question_options_order UNIQUE (question_id, order_index),
    CONSTRAINT fk_question_options_question FOREIGN KEY (question_id) REFERENCES questions (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS questionnaire_responses (
    id BIGSERIAL PRIMARY KEY,
    reference_id CHARACTER VARYING(255) NOT NULL,
    questionnaire_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    started_at TIMESTAMP(6) WITH TIME ZONE,
    completed_at TIMESTAMP(6) WITH TIME ZONE,
    last_modified_at TIMESTAMP(6) WITH TIME ZONE,
    total_score DECIMAL(10,2),
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_questionnaire_responses_reference_id UNIQUE (reference_id),
    CONSTRAINT uq_questionnaire_responses_user_questionnaire UNIQUE (questionnaire_id, user_id),
    CONSTRAINT fk_questionnaire_responses_questionnaire FOREIGN KEY (questionnaire_id) REFERENCES questionnaires (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_questionnaire_responses_user FOREIGN KEY (user_id) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS question_responses (
    id BIGSERIAL PRIMARY KEY,
    reference_id CHARACTER VARYING(255) NOT NULL,
    questionnaire_response_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    answer_value TEXT,
    is_skipped BOOLEAN NOT NULL DEFAULT FALSE,
    responded_at TIMESTAMP(6) WITH TIME ZONE,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_question_responses_reference_id UNIQUE (reference_id),
    CONSTRAINT uq_question_responses_response_question UNIQUE (questionnaire_response_id, question_id),
    CONSTRAINT fk_question_responses_questionnaire_response FOREIGN KEY (questionnaire_response_id) REFERENCES questionnaire_responses (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_question_responses_question FOREIGN KEY (question_id) REFERENCES questions (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);

-- Add self-referencing foreign keys for users table after all tables are created
ALTER TABLE users ADD CONSTRAINT fk_users_created_by FOREIGN KEY (created_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE users ADD CONSTRAINT fk_users_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS index_users_email ON users (email);
CREATE INDEX IF NOT EXISTS index_users_user_id ON users (user_id);
CREATE INDEX IF NOT EXISTS index_confirmations_user_id ON confirmations (user_id);
CREATE INDEX IF NOT EXISTS index_credentials_user_id ON credentials (user_id);
CREATE INDEX IF NOT EXISTS index_user_roles_user_id ON user_roles (user_id);
CREATE INDEX IF NOT EXISTS index_user_roles_role_id ON user_roles (role_id);
CREATE INDEX IF NOT EXISTS index_chat_rooms_user1_id ON chat_rooms (user1_id);
CREATE INDEX IF NOT EXISTS index_chat_rooms_user2_id ON chat_rooms (user2_id);
CREATE INDEX IF NOT EXISTS index_chat_rooms_active ON chat_rooms (is_active);
CREATE INDEX IF NOT EXISTS index_chat_messages_chat_room_id ON chat_messages (chat_room_id);
CREATE INDEX IF NOT EXISTS index_chat_messages_sender_id ON chat_messages (sender_id);
CREATE INDEX IF NOT EXISTS index_chat_messages_created_at ON chat_messages (created_at);
CREATE INDEX IF NOT EXISTS index_chat_messages_is_read ON chat_messages (is_read);
CREATE INDEX IF NOT EXISTS index_chat_room_documents_chat_room_id ON chat_room_documents (chat_room_id);
CREATE INDEX IF NOT EXISTS index_chat_room_documents_document_id ON chat_room_documents (document_id);
CREATE INDEX IF NOT EXISTS index_document_analysis_document_id ON document_analysis (document_id);
CREATE INDEX IF NOT EXISTS idx_questionnaires_category ON questionnaires(category);
CREATE INDEX IF NOT EXISTS idx_questionnaires_active ON questionnaires(is_active);
CREATE INDEX IF NOT EXISTS idx_questionnaires_created_at ON questionnaires(created_at);
CREATE INDEX IF NOT EXISTS idx_question_pages_questionnaire ON question_pages(questionnaire_id);
CREATE INDEX IF NOT EXISTS idx_questions_page ON questions(page_id);
CREATE INDEX IF NOT EXISTS idx_question_options_question ON question_options(question_id);
CREATE INDEX IF NOT EXISTS idx_questionnaire_responses_user ON questionnaire_responses(user_id);
CREATE INDEX IF NOT EXISTS idx_questionnaire_responses_questionnaire ON questionnaire_responses(questionnaire_id);
CREATE INDEX IF NOT EXISTS idx_question_responses_questionnaire_response ON question_responses(questionnaire_response_id);
CREATE INDEX IF NOT EXISTS idx_question_responses_question ON question_responses(question_id);

COMMIT;