ALTER TABLE notifications
    ADD COLUMN action_url VARCHAR(255);

ALTER TABLE notifications
    ADD COLUMN dedupe_key VARCHAR(255);

CREATE UNIQUE INDEX uk_notifications_dedupe_key ON notifications(dedupe_key);
