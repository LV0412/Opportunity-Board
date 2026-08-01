CREATE TABLE users (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    role VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL
);

CREATE TABLE student_profiles (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    university VARCHAR(150),
    major VARCHAR(120),
    graduation_year INTEGER,
    location VARCHAR(120),
    bio TEXT
);

CREATE TABLE organization_profiles (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    organization_name VARCHAR(160) NOT NULL,
    industry VARCHAR(120),
    website_url VARCHAR(255),
    logo_url VARCHAR(255),
    description TEXT,
    verified BOOLEAN NOT NULL
);

CREATE TABLE opportunity_categories (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    name VARCHAR(80) NOT NULL UNIQUE,
    slug VARCHAR(90) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE tags (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    name VARCHAR(60) NOT NULL UNIQUE,
    slug VARCHAR(70) NOT NULL UNIQUE
);

CREATE TABLE skills (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    name VARCHAR(80) NOT NULL UNIQUE,
    slug VARCHAR(90) NOT NULL UNIQUE
);

CREATE TABLE opportunities (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    organization_id UUID NOT NULL REFERENCES organization_profiles(id),
    category_id UUID NOT NULL REFERENCES opportunity_categories(id),
    title VARCHAR(180) NOT NULL,
    description TEXT NOT NULL,
    requirements TEXT,
    location VARCHAR(120),
    remote BOOLEAN NOT NULL,
    apply_url VARCHAR(255),
    deadline_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(30) NOT NULL
);

CREATE TABLE opportunity_tags (
    opportunity_id UUID NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (opportunity_id, tag_id)
);

CREATE TABLE student_skills (
    student_profile_id UUID NOT NULL REFERENCES student_profiles(id) ON DELETE CASCADE,
    skill_id UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    PRIMARY KEY (student_profile_id, skill_id)
);

CREATE TABLE resumes (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    student_id UUID NOT NULL REFERENCES student_profiles(id),
    file_name VARCHAR(120) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    primary_resume BOOLEAN NOT NULL
);

CREATE TABLE applications (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    student_id UUID NOT NULL REFERENCES student_profiles(id),
    opportunity_id UUID NOT NULL REFERENCES opportunities(id),
    resume_id UUID REFERENCES resumes(id),
    cover_letter TEXT,
    status VARCHAR(30) NOT NULL,
    CONSTRAINT uk_applications_student_opportunity UNIQUE (student_id, opportunity_id)
);

CREATE TABLE bookmarks (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    student_id UUID NOT NULL REFERENCES student_profiles(id),
    opportunity_id UUID NOT NULL REFERENCES opportunities(id),
    CONSTRAINT uk_bookmarks_student_opportunity UNIQUE (student_id, opportunity_id)
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    type VARCHAR(40) NOT NULL,
    title VARCHAR(180) NOT NULL,
    message TEXT NOT NULL,
    read_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE reports (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    opportunity_id UUID NOT NULL REFERENCES opportunities(id),
    reporter_id UUID NOT NULL REFERENCES users(id),
    reason VARCHAR(120) NOT NULL,
    description TEXT,
    status VARCHAR(30) NOT NULL
);

CREATE TABLE admin_reviews (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    opportunity_id UUID NOT NULL REFERENCES opportunities(id),
    admin_id UUID NOT NULL REFERENCES users(id),
    status VARCHAR(30) NOT NULL,
    note TEXT
);

CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_opportunities_status ON opportunities(status);
CREATE INDEX idx_opportunities_category_status ON opportunities(category_id, status);
CREATE INDEX idx_opportunities_organization ON opportunities(organization_id);
CREATE INDEX idx_applications_student ON applications(student_id);
CREATE INDEX idx_applications_opportunity ON applications(opportunity_id);
CREATE INDEX idx_bookmarks_student ON bookmarks(student_id);
CREATE INDEX idx_notifications_user_read ON notifications(user_id, read_at);
CREATE INDEX idx_reports_status ON reports(status);
