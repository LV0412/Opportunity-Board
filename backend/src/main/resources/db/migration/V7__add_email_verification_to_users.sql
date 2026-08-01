ALTER TABLE users
    ADD COLUMN email_verified_at TIMESTAMP WITH TIME ZONE;

ALTER TABLE users
    ADD COLUMN email_verification_token VARCHAR(120);

ALTER TABLE users
    ADD COLUMN email_verification_token_expires_at TIMESTAMP WITH TIME ZONE;

UPDATE users
SET email_verified_at = CURRENT_TIMESTAMP
WHERE status = 'ACTIVE';

CREATE UNIQUE INDEX idx_users_email_verification_token
    ON users(email_verification_token);
