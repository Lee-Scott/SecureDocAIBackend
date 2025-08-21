-- SQL to create missing questionnaire system tables
-- Run this in your PostgreSQL database

BEGIN;

-- Questionnaires table
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
    CONSTRAINT fk_questionnaires_created_by_user FOREIGN KEY (created_by_user_id) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_questionnaires_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_questionnaires_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT check_questionnaire_category CHECK (category IN ('HEALTHCARE', 'EDUCATION', 'BUSINESS', 'PERSONAL', 'RESEARCH', 'OTHER'))
);

-- Question pages table
CREATE TABLE IF NOT EXISTS question_pages (
    id BIGSERIAL PRIMARY KEY,
    reference_id CHARACTER VARYING(255) NOT NULL,
    questionnaire_id BIGINT NOT NULL,
    page_number INTEGER NOT NULL,
    title CHARACTER VARYING(255) NOT NULL,
    description TEXT,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_question_pages_reference_id UNIQUE (reference_id),
    CONSTRAINT uq_question_pages_questionnaire_page UNIQUE (questionnaire_id, page_number),
    CONSTRAINT fk_question_pages_questionnaire_id FOREIGN KEY (questionnaire_id) REFERENCES questionnaires (id) ON DELETE CASCADE,
    CONSTRAINT fk_question_pages_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_question_pages_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE RESTRICT
);

-- Questions table
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
    CONSTRAINT fk_questions_page_id FOREIGN KEY (page_id) REFERENCES question_pages (id) ON DELETE CASCADE,
    CONSTRAINT fk_questions_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_questions_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT check_question_type CHECK (question_type IN ('MULTIPLE_CHOICE', 'SINGLE_CHOICE', 'TEXT', 'NUMBER', 'DATE', 'BOOLEAN', 'SCALE', 'TEXTAREA'))
);

-- Question validation rules table
CREATE TABLE IF NOT EXISTS question_validation_rules (
    question_id BIGINT NOT NULL,
    validation_rules_key CHARACTER VARYING(255) NOT NULL,
    validation_rules CHARACTER VARYING(255),
    CONSTRAINT fk_question_validation_rules_question_id FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE
);

-- Question options table
CREATE TABLE IF NOT EXISTS question_options (
    id BIGSERIAL PRIMARY KEY,
    reference_id CHARACTER VARYING(255) NOT NULL,
    question_id BIGINT NOT NULL,
    option_text CHARACTER VARYING(500) NOT NULL,
    option_value CHARACTER VARYING(255) NOT NULL,
    display_order INTEGER NOT NULL,
    score_value DOUBLE PRECISION,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_question_options_reference_id UNIQUE (reference_id),
    CONSTRAINT uq_question_options_question_order UNIQUE (question_id, display_order),
    CONSTRAINT fk_question_options_question_id FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE,
    CONSTRAINT fk_question_options_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_question_options_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE RESTRICT
);

-- Questionnaire responses table
CREATE TABLE IF NOT EXISTS questionnaire_responses (
    id BIGSERIAL PRIMARY KEY,
    reference_id CHARACTER VARYING(255) NOT NULL,
    questionnaire_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    started_at TIMESTAMP(6) WITH TIME ZONE,
    completed_at TIMESTAMP(6) WITH TIME ZONE,
    last_modified_at TIMESTAMP(6) WITH TIME ZONE,
    total_score DOUBLE PRECISION,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_questionnaire_responses_reference_id UNIQUE (reference_id),
    CONSTRAINT uq_questionnaire_responses_user_questionnaire UNIQUE (questionnaire_id, user_id),
    CONSTRAINT fk_questionnaire_responses_questionnaire_id FOREIGN KEY (questionnaire_id) REFERENCES questionnaires (id) ON DELETE CASCADE,
    CONSTRAINT fk_questionnaire_responses_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_questionnaire_responses_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_questionnaire_responses_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE RESTRICT
);

-- Question responses table
CREATE TABLE IF NOT EXISTS question_responses (
    id BIGSERIAL PRIMARY KEY,
    reference_id CHARACTER VARYING(255) NOT NULL,
    questionnaire_response_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    response_text TEXT,
    response_number DOUBLE PRECISION,
    response_date DATE,
    response_boolean BOOLEAN,
    selected_options TEXT, -- JSON array of selected option IDs
    score DOUBLE PRECISION,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_question_responses_reference_id UNIQUE (reference_id),
    CONSTRAINT uq_question_responses_questionnaire_question UNIQUE (questionnaire_response_id, question_id),
    CONSTRAINT fk_question_responses_questionnaire_response_id FOREIGN KEY (questionnaire_response_id) REFERENCES questionnaire_responses (id) ON DELETE CASCADE,
    CONSTRAINT fk_question_responses_question_id FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE,
    CONSTRAINT fk_question_responses_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_question_responses_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE RESTRICT
);

-- Add unique constraint on email if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'uq_users_email'
        AND table_name = 'users'
    ) THEN
        ALTER TABLE users ADD CONSTRAINT uq_users_email UNIQUE (email);
    END IF;
END $$;

COMMIT;
