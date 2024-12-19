-- Sets up the schema. This runs before data.sql
BEGIN:

CREATE TABLE IF NOT EXISTS test (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255)
  )