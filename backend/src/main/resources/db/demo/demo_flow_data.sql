-- Demo accounts all use the password: password.
-- Fixed UUIDs make this seed deterministic across development environments.

INSERT INTO users (id, created_at, updated_at, email, password_hash, full_name, role, status) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin@opportunity.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Demo Administrator', 'ADMIN', 'ACTIVE'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'student@opportunity.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nguyen Minh Anh', 'STUDENT', 'ACTIVE'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'student2@opportunity.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Tran Gia Bao', 'STUDENT', 'ACTIVE'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'locked.student@opportunity.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Locked Student', 'STUDENT', 'LOCKED'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa5', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'organization@opportunity.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'FPT Software Talent Team', 'ORGANIZATION', 'ACTIVE'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'organization2@opportunity.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Green Future Foundation', 'ORGANIZATION', 'ACTIVE'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa7', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'disabled.organization@opportunity.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Disabled Organization', 'ORGANIZATION', 'DISABLED')
ON CONFLICT DO NOTHING;

INSERT INTO student_profiles (id, created_at, updated_at, user_id, university, major, graduation_year, location, bio, interests) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'FPT University', 'Software Engineering', 2027, 'Ho Chi Minh City', 'Full-stack student interested in products with social impact.', 'Java, React, hackathons, startup internships'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb202', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'University of Economics Ho Chi Minh City', 'Business Analytics', 2026, 'Ho Chi Minh City', 'Data enthusiast looking for scholarships and competitions.', 'Data analysis, scholarships, case competitions'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb203', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', 'Demo University', 'Information Systems', 2028, 'Da Nang', 'Account used to test locked-user access.', 'Technology')
ON CONFLICT DO NOTHING;

INSERT INTO organization_profiles (id, created_at, updated_at, user_id, organization_name, industry, website_url, logo_url, description, verified) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb101', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa5', 'FPT Software Talent', 'Information Technology', 'https://fptsoftware.com', 'https://placehold.co/256x256?text=FPT', 'Technology employer offering internships and graduate opportunities.', TRUE),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb102', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa6', 'Green Future Foundation', 'Non-profit', 'https://example.org/green-future', 'https://placehold.co/256x256?text=GFF', 'Student programs focused on sustainability and community innovation.', FALSE),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb103', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa7', 'Disabled Demo Organization', 'Education', NULL, NULL, 'Organization used to test disabled-user access.', FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO student_skills (student_profile_id, skill_id) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', '33333333-3333-3333-3333-333333333331'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', '33333333-3333-3333-3333-333333333332'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', '33333333-3333-3333-3333-333333333334'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb202', '33333333-3333-3333-3333-333333333333'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb202', '33333333-3333-3333-3333-333333333335')
ON CONFLICT DO NOTHING;

INSERT INTO resumes (id, created_at, updated_at, student_id, file_name, file_url, primary_resume) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb301', CURRENT_TIMESTAMP - INTERVAL '30' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', 'nguyen-minh-anh-cv.pdf', 'https://example.org/demo/nguyen-minh-anh-cv.pdf', TRUE),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb302', CURRENT_TIMESTAMP - INTERVAL '20' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', 'nguyen-minh-anh-portfolio.pdf', 'https://example.org/demo/nguyen-minh-anh-portfolio.pdf', FALSE),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb303', CURRENT_TIMESTAMP - INTERVAL '15' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb202', 'tran-gia-bao-cv.pdf', 'https://example.org/demo/tran-gia-bao-cv.pdf', TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO opportunities (id, created_at, updated_at, organization_id, category_id, title, description, requirements, location, remote, apply_url, deadline_at, status, view_count) VALUES
('cccccccc-cccc-cccc-cccc-cccccccccc01', CURRENT_TIMESTAMP - INTERVAL '14' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb101', '11111111-1111-1111-1111-111111111111', 'Java Backend Intern 2026', 'Join a product team building scalable services for regional customers.', 'Java fundamentals, SQL, Git, and willingness to learn Spring Boot.', 'Ho Chi Minh City', TRUE, NULL, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 'APPROVED', 248),
('cccccccc-cccc-cccc-cccc-cccccccccc02', CURRENT_TIMESTAMP - INTERVAL '10' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb101', '11111111-1111-1111-1111-111111111114', 'AI for Social Good Hackathon', 'Build a prototype that improves access to education or public services.', 'Team of 2-4 students, and a working demo is required.', 'Online', TRUE, 'https://example.org/apply/ai-hackathon', CURRENT_TIMESTAMP + INTERVAL '5' DAY, 'APPROVED', 531),
('cccccccc-cccc-cccc-cccc-cccccccccc03', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb101', '11111111-1111-1111-1111-111111111112', 'Junior Product Associate', 'Early-career product role working with engineering and design teams.', 'Strong communication and product thinking.', 'Ha Noi', FALSE, NULL, CURRENT_TIMESTAMP + INTERVAL '45' DAY, 'PENDING', 12),
('cccccccc-cccc-cccc-cccc-cccccccccc04', CURRENT_TIMESTAMP - INTERVAL '3' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb102', '11111111-1111-1111-1111-111111111115', 'Green Leaders Scholarship', 'Scholarship for students leading measurable sustainability initiatives.', 'Current university student with an active community project.', 'Vietnam', TRUE, 'https://example.org/apply/green-scholarship', CURRENT_TIMESTAMP + INTERVAL '60' DAY, 'DRAFT', 0),
('cccccccc-cccc-cccc-cccc-cccccccccc05', CURRENT_TIMESTAMP - INTERVAL '20' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb102', '11111111-1111-1111-1111-111111111113', 'Sustainable Campus Challenge', 'National case competition for practical low-carbon campus solutions.', 'Cross-disciplinary student teams are encouraged.', 'Da Nang', FALSE, NULL, CURRENT_TIMESTAMP + INTERVAL '20' DAY, 'REJECTED', 35),
('cccccccc-cccc-cccc-cccc-cccccccccc06', CURRENT_TIMESTAMP - INTERVAL '90' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb101', '11111111-1111-1111-1111-111111111111', 'Frontend Internship Spring Cohort', 'Completed internship cohort retained for history and dashboard metrics.', 'React and TypeScript fundamentals.', 'Ho Chi Minh City', FALSE, NULL, CURRENT_TIMESTAMP - INTERVAL '10' DAY, 'CLOSED', 410),
('cccccccc-cccc-cccc-cccc-cccccccccc07', CURRENT_TIMESTAMP - INTERVAL '7' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb102', '11111111-1111-1111-1111-111111111117', 'Climate Startup Incubator', 'Twelve-week incubation program with mentors and investor office hours.', 'Student-led startup with an MVP or validated problem.', 'Singapore / Remote', TRUE, 'https://example.org/apply/climate-incubator', CURRENT_TIMESTAMP + INTERVAL '90' DAY, 'APPROVED', 189)
ON CONFLICT DO NOTHING;

INSERT INTO opportunity_tags (opportunity_id, tag_id) VALUES
('cccccccc-cccc-cccc-cccc-cccccccccc01', '22222222-2222-2222-2222-222222222221'),
('cccccccc-cccc-cccc-cccc-cccccccccc01', '22222222-2222-2222-2222-222222222222'),
('cccccccc-cccc-cccc-cccc-cccccccccc01', '22222222-2222-2222-2222-222222222223'),
('cccccccc-cccc-cccc-cccc-cccccccccc02', '22222222-2222-2222-2222-222222222221'),
('cccccccc-cccc-cccc-cccc-cccccccccc02', '22222222-2222-2222-2222-222222222225'),
('cccccccc-cccc-cccc-cccc-cccccccccc03', '22222222-2222-2222-2222-222222222224'),
('cccccccc-cccc-cccc-cccc-cccccccccc07', '22222222-2222-2222-2222-222222222221'),
('cccccccc-cccc-cccc-cccc-cccccccccc07', '22222222-2222-2222-2222-222222222224'),
('cccccccc-cccc-cccc-cccc-cccccccccc07', '22222222-2222-2222-2222-222222222225')
ON CONFLICT DO NOTHING;

INSERT INTO bookmarks (id, created_at, updated_at, student_id, opportunity_id) VALUES
('dddddddd-dddd-dddd-dddd-dddddddddd01', CURRENT_TIMESTAMP - INTERVAL '4' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', 'cccccccc-cccc-cccc-cccc-cccccccccc02'),
('dddddddd-dddd-dddd-dddd-dddddddddd02', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', 'cccccccc-cccc-cccc-cccc-cccccccccc07'),
('dddddddd-dddd-dddd-dddd-dddddddddd03', CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb202', 'cccccccc-cccc-cccc-cccc-cccccccccc01')
ON CONFLICT DO NOTHING;

INSERT INTO applications (id, created_at, updated_at, student_id, opportunity_id, resume_id, cover_letter, status) VALUES
('dddddddd-dddd-dddd-dddd-dddddddddd11', CURRENT_TIMESTAMP - INTERVAL '12' DAY, CURRENT_TIMESTAMP - INTERVAL '12' DAY, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', 'cccccccc-cccc-cccc-cccc-cccccccccc01', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb301', 'I would like to contribute my Java and React experience while learning from the backend team.', 'APPLIED'),
('dddddddd-dddd-dddd-dddd-dddddddddd12', CURRENT_TIMESTAMP - INTERVAL '8' DAY, CURRENT_TIMESTAMP - INTERVAL '3' DAY, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb202', 'cccccccc-cccc-cccc-cccc-cccccccccc02', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb303', 'My analytics background can help the team validate impact with evidence.', 'REVIEWING'),
('dddddddd-dddd-dddd-dddd-dddddddddd13', CURRENT_TIMESTAMP - INTERVAL '25' DAY, CURRENT_TIMESTAMP - INTERVAL '5' DAY, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', 'cccccccc-cccc-cccc-cccc-cccccccccc07', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb301', 'I am building a student sustainability tool and would value the incubator mentorship.', 'ACCEPTED'),
('dddddddd-dddd-dddd-dddd-dddddddddd14', CURRENT_TIMESTAMP - INTERVAL '40' DAY, CURRENT_TIMESTAMP - INTERVAL '15' DAY, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb202', 'cccccccc-cccc-cccc-cccc-cccccccccc06', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb303', 'I am interested in applying analytics to frontend product decisions.', 'REJECTED')
ON CONFLICT DO NOTHING;

INSERT INTO notifications (id, created_at, updated_at, user_id, type, title, message, read_at, action_url, dedupe_key) VALUES
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee01', CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'DEADLINE_REMINDER', 'Deadline approaching', 'AI for Social Good Hackathon closes in 5 days.', NULL, '/opportunities/cccccccc-cccc-cccc-cccc-cccccccccc02', 'demo-deadline-student-1'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee02', CURRENT_TIMESTAMP - INTERVAL '3' DAY, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'APPLICATION_STATUS', 'Application under review', 'Your application is now being reviewed.', NULL, '/student/applications', 'demo-application-reviewing-student-2'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee03', CURRENT_TIMESTAMP - INTERVAL '6' DAY, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa5', 'OPPORTUNITY_APPROVED', 'Opportunity approved', 'Java Backend Intern 2026 was approved and is now public.', CURRENT_TIMESTAMP - INTERVAL '5' DAY, '/organization/opportunities', 'demo-opportunity-approved-org-1'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee04', CURRENT_TIMESTAMP - INTERVAL '19' DAY, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa6', 'OPPORTUNITY_REJECTED', 'Opportunity needs changes', 'Sustainable Campus Challenge was rejected. Review the admin note.', NULL, '/organization/opportunities', 'demo-opportunity-rejected-org-2'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee05', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'WEEKLY_DIGEST', 'Your weekly opportunity digest', 'Three new opportunities match your interests.', CURRENT_TIMESTAMP - INTERVAL '1' DAY, '/explore', 'demo-weekly-digest-student-1'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee06', CURRENT_TIMESTAMP - INTERVAL '30' DAY, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'SYSTEM', 'Demo environment ready', 'Sample data is available for end-to-end testing.', CURRENT_TIMESTAMP - INTERVAL '29' DAY, '/admin/dashboard', 'demo-system-admin')
ON CONFLICT DO NOTHING;

INSERT INTO reports (id, created_at, updated_at, opportunity_id, reporter_id, reason, description, status) VALUES
('ffffffff-ffff-ffff-ffff-fffffffffff1', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP, 'cccccccc-cccc-cccc-cccc-cccccccccc01', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'Information may be outdated', 'Please verify whether this position is still accepting applications.', 'PENDING'),
('ffffffff-ffff-ffff-ffff-fffffffffff2', CURRENT_TIMESTAMP - INTERVAL '12' DAY, CURRENT_TIMESTAMP - INTERVAL '8' DAY, 'cccccccc-cccc-cccc-cccc-cccccccccc02', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'Broken external link', 'The registration link was temporarily unavailable.', 'RESOLVED'),
('ffffffff-ffff-ffff-ffff-fffffffffff3', CURRENT_TIMESTAMP - INTERVAL '15' DAY, CURRENT_TIMESTAMP - INTERVAL '10' DAY, 'cccccccc-cccc-cccc-cccc-cccccccccc07', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'Suspected duplicate', 'This looked similar to another program but was verified as distinct.', 'REJECTED')
ON CONFLICT DO NOTHING;

INSERT INTO admin_reviews (id, created_at, updated_at, opportunity_id, admin_id, status, note) VALUES
('ffffffff-ffff-ffff-ffff-ffffffffff11', CURRENT_TIMESTAMP - INTERVAL '14' DAY, CURRENT_TIMESTAMP - INTERVAL '13' DAY, 'cccccccc-cccc-cccc-cccc-cccccccccc01', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'APPROVED', 'Requirements and application details verified.'),
('ffffffff-ffff-ffff-ffff-ffffffffff12', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP, 'cccccccc-cccc-cccc-cccc-cccccccccc03', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'PENDING', 'Awaiting verification of the hiring contact.'),
('ffffffff-ffff-ffff-ffff-ffffffffff13', CURRENT_TIMESTAMP - INTERVAL '20' DAY, CURRENT_TIMESTAMP - INTERVAL '19' DAY, 'cccccccc-cccc-cccc-cccc-cccccccccc05', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'REJECTED', 'Application terms were incomplete, so the organization can revise and resubmit.')
ON CONFLICT DO NOTHING;

INSERT INTO admin_audit_logs (id, created_at, updated_at, admin_id, action, target_type, target_id, details) VALUES
('99999999-9999-9999-9999-999999999901', CURRENT_TIMESTAMP - INTERVAL '13' DAY, CURRENT_TIMESTAMP - INTERVAL '13' DAY, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'APPROVE_OPPORTUNITY', 'OPPORTUNITY', 'cccccccc-cccc-cccc-cccc-cccccccccc01', 'Approved demo Java internship.'),
('99999999-9999-9999-9999-999999999902', CURRENT_TIMESTAMP - INTERVAL '19' DAY, CURRENT_TIMESTAMP - INTERVAL '19' DAY, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'REJECT_OPPORTUNITY', 'OPPORTUNITY', 'cccccccc-cccc-cccc-cccc-cccccccccc05', 'Rejected incomplete competition listing.'),
('99999999-9999-9999-9999-999999999903', CURRENT_TIMESTAMP - INTERVAL '8' DAY, CURRENT_TIMESTAMP - INTERVAL '8' DAY, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'RESOLVE_REPORT', 'REPORT', 'ffffffff-ffff-ffff-ffff-fffffffffff2', 'Confirmed external link is working again.'),
('99999999-9999-9999-9999-999999999904', CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP - INTERVAL '1' DAY, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'LOCK_USER', 'USER', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', 'Locked demo user for access-control testing.')
ON CONFLICT DO NOTHING;
