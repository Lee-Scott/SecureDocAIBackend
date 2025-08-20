-- Drop existing foreign key constraints if they exist
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'document_locks') THEN
        IF EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name = 'document_locks' AND constraint_name = 'fk_document_locks_document') THEN
            ALTER TABLE document_locks DROP CONSTRAINT fk_document_locks_document;
        END IF;
        IF EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE table_name = 'document_locks' AND constraint_name = 'fk_document_locks_user') THEN
            ALTER TABLE document_locks DROP CONSTRAINT fk_document_locks_user;
        END IF;
        DROP TABLE IF EXISTS document_locks CASCADE;
    END IF;
END $$;

-- Recreate the table with the correct schema
CREATE TABLE IF NOT EXISTS document_locks (
    id BIGSERIAL PRIMARY KEY,
    document_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    lock_timestamp TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_document_locks_document FOREIGN KEY (document_id) REFERENCES documents(document_id) ON DELETE CASCADE,
    CONSTRAINT fk_document_locks_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT uk_document_locks_document UNIQUE (document_id)
);

-- Add index for performance
CREATE INDEX IF NOT EXISTS idx_document_locks_user ON document_locks(user_id);

-- Create trigger function for updated_at
CREATE OR REPLACE FUNCTION update_modified_column() 
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW; 
END;
$$ language 'plpgsql';

-- Create trigger
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'update_document_locks_updated_at') THEN
        CREATE TRIGGER update_document_locks_updated_at
        BEFORE UPDATE ON document_locks
        FOR EACH ROW EXECUTE FUNCTION update_modified_column();
    END IF;
END $$;
