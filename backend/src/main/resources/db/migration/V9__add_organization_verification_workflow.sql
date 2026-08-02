ALTER TABLE organization_profiles
    ADD COLUMN verification_status VARCHAR(30) NOT NULL DEFAULT 'UNVERIFIED';
ALTER TABLE organization_profiles ADD COLUMN verification_note TEXT;
ALTER TABLE organization_profiles ADD COLUMN verification_requested_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE organization_profiles ADD COLUMN verified_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE organization_profiles ADD COLUMN verified_by UUID;
ALTER TABLE organization_profiles
    ADD CONSTRAINT fk_organization_profiles_verified_by FOREIGN KEY (verified_by) REFERENCES users(id);

UPDATE organization_profiles
SET verification_status = CASE WHEN verified THEN 'VERIFIED' ELSE 'UNVERIFIED' END,
    verified_at = CASE WHEN verified THEN updated_at ELSE NULL END;

ALTER TABLE organization_profiles DROP COLUMN verified;

CREATE INDEX idx_organization_profiles_verification_status
    ON organization_profiles(verification_status);
