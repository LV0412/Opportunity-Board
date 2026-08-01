CREATE TABLE admin_audit_logs (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    admin_id UUID NOT NULL REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    target_type VARCHAR(60) NOT NULL,
    target_id UUID,
    details TEXT
);

CREATE INDEX idx_admin_audit_logs_admin_created ON admin_audit_logs(admin_id, created_at DESC);
