-- Demo accounts all use the password: password.
-- Fixed UUIDs make this seed deterministic across development environments.

BEGIN;
SELECT pg_advisory_xact_lock(hashtext('opportunity-board-demo-seed'));

DO $$
BEGIN
    IF (SELECT COUNT(*) FROM opportunity_categories WHERE id::text LIKE '11111111-%') < 7 THEN
        RAISE EXCEPTION 'Reference categories are missing. Run Flyway migrations before loading demo data.';
    END IF;
    IF (SELECT COUNT(*) FROM tags WHERE id::text LIKE '22222222-%') < 5 THEN
        RAISE EXCEPTION 'Reference tags are missing. Run Flyway migrations before loading demo data.';
    END IF;
    IF (SELECT COUNT(*) FROM skills WHERE id::text LIKE '33333333-%') < 5 THEN
        RAISE EXCEPTION 'Reference skills are missing. Run Flyway migrations before loading demo data.';
    END IF;
END $$;

-- Replace all business/demo data while preserving Flyway history and reference taxonomy.
TRUNCATE TABLE
    admin_audit_logs,
    admin_reviews,
    reports,
    notifications,
    applications,
    bookmarks,
    opportunity_tags,
    opportunities,
    resumes,
    student_skills,
    organization_profiles,
    student_profiles,
    users
CASCADE;

INSERT INTO users (id, created_at, updated_at, email, password_hash, full_name, role, status) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin@opportunity.local', '$2a$10$YLtMjMLOufAlWraMmcB3iePz0a0jDz9L12UWFXf6hxbUeI/JoraQS', 'Demo Administrator', 'ADMIN', 'ACTIVE'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'student@opportunity.local', '$2a$10$YLtMjMLOufAlWraMmcB3iePz0a0jDz9L12UWFXf6hxbUeI/JoraQS', 'Nguyen Minh Anh', 'STUDENT', 'ACTIVE'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'student2@opportunity.local', '$2a$10$YLtMjMLOufAlWraMmcB3iePz0a0jDz9L12UWFXf6hxbUeI/JoraQS', 'Tran Gia Bao', 'STUDENT', 'ACTIVE'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'locked.student@opportunity.local', '$2a$10$YLtMjMLOufAlWraMmcB3iePz0a0jDz9L12UWFXf6hxbUeI/JoraQS', 'Locked Student', 'STUDENT', 'LOCKED'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa5', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'organization@opportunity.local', '$2a$10$YLtMjMLOufAlWraMmcB3iePz0a0jDz9L12UWFXf6hxbUeI/JoraQS', 'FPT Software Talent Team', 'ORGANIZATION', 'ACTIVE'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'organization2@opportunity.local', '$2a$10$YLtMjMLOufAlWraMmcB3iePz0a0jDz9L12UWFXf6hxbUeI/JoraQS', 'Green Future Foundation', 'ORGANIZATION', 'ACTIVE'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa7', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'disabled.organization@opportunity.local', '$2a$10$YLtMjMLOufAlWraMmcB3iePz0a0jDz9L12UWFXf6hxbUeI/JoraQS', 'Disabled Organization', 'ORGANIZATION', 'DISABLED'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa8', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'student3@opportunity.local', '$2a$10$YLtMjMLOufAlWraMmcB3iePz0a0jDz9L12UWFXf6hxbUeI/JoraQS', 'Le Hoang Phuong', 'STUDENT', 'ACTIVE'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa9', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'organization3@opportunity.local', '$2a$10$YLtMjMLOufAlWraMmcB3iePz0a0jDz9L12UWFXf6hxbUeI/JoraQS', 'VNG Campus Recruitment', 'ORGANIZATION', 'ACTIVE'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa0', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'student4@opportunity.local', '$2a$10$YLtMjMLOufAlWraMmcB3iePz0a0jDz9L12UWFXf6hxbUeI/JoraQS', 'Pham Thu Trang', 'STUDENT', 'ACTIVE')
ON CONFLICT DO NOTHING;

UPDATE users
SET email_verified_at = CURRENT_TIMESTAMP
WHERE status = 'ACTIVE';

INSERT INTO student_profiles (id, created_at, updated_at, user_id, university, major, graduation_year, location, bio, interests) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'FPT University', 'Software Engineering', 2027, 'Ho Chi Minh City', 'Full-stack student interested in products with social impact.', 'Java, React, hackathons, startup internships'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb202', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'University of Economics Ho Chi Minh City', 'Business Analytics', 2026, 'Ho Chi Minh City', 'Data enthusiast looking for scholarships and competitions.', 'Data analysis, scholarships, case competitions'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb203', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', 'Demo University', 'Information Systems', 2028, 'Da Nang', 'Account used to test locked-user access.', 'Technology'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb204', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa8', 'Ho Chi Minh City University of Technology', 'Computer Science', 2026, 'Ho Chi Minh City', 'Backend developer focused on distributed systems and cloud platforms.', 'Java, cloud computing, international internships'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb205', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa0', 'RMIT University Vietnam', 'Digital Marketing', 2027, 'Ha Noi', 'Creative student combining marketing analytics with sustainable business.', 'Marketing, analytics, sustainability, competitions')
ON CONFLICT DO NOTHING;

INSERT INTO organization_profiles (
    id, created_at, updated_at, user_id, organization_name, industry, website_url, logo_url, description,
    verification_status, verification_note, verification_requested_at, verified_at, verified_by
) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb101', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa5', 'FPT Software Talent', 'Information Technology', 'https://fptsoftware.com', 'https://placehold.co/256x256?text=FPT', 'Technology employer offering internships and graduate opportunities.', 'VERIFIED', NULL, CURRENT_TIMESTAMP - INTERVAL '40' DAY, CURRENT_TIMESTAMP - INTERVAL '39' DAY, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb102', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa6', 'Green Future Foundation', 'Non-profit', 'https://example.org/green-future', 'https://placehold.co/256x256?text=GFF', 'Student programs focused on sustainability and community innovation.', 'PENDING', NULL, CURRENT_TIMESTAMP - INTERVAL '2' DAY, NULL, NULL),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb103', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa7', 'Disabled Demo Organization', 'Education', NULL, NULL, 'Organization used to test disabled-user access.', 'UNVERIFIED', NULL, NULL, NULL, NULL),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb104', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa9', 'VNG Campus Recruitment', 'Technology and Digital Services', 'https://vng.com.vn', 'https://placehold.co/256x256?text=VNG', 'University recruitment team offering engineering, product, data, and design programs.', 'REJECTED', 'Website information needs to be updated before verification.', CURRENT_TIMESTAMP - INTERVAL '5' DAY, NULL, NULL)
ON CONFLICT DO NOTHING;

INSERT INTO student_skills (student_profile_id, skill_id) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', '33333333-3333-3333-3333-333333333331'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', '33333333-3333-3333-3333-333333333332'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', '33333333-3333-3333-3333-333333333334'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb202', '33333333-3333-3333-3333-333333333333'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb202', '33333333-3333-3333-3333-333333333335'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb204', '33333333-3333-3333-3333-333333333331'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb204', '33333333-3333-3333-3333-333333333335'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb205', '33333333-3333-3333-3333-333333333333'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb205', '33333333-3333-3333-3333-333333333335')
ON CONFLICT DO NOTHING;

INSERT INTO resumes (id, created_at, updated_at, student_id, file_name, file_url, primary_resume) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb301', CURRENT_TIMESTAMP - INTERVAL '30' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', 'nguyen-minh-anh-cv.pdf', 'https://example.org/demo/nguyen-minh-anh-cv.pdf', TRUE),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb302', CURRENT_TIMESTAMP - INTERVAL '20' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', 'nguyen-minh-anh-portfolio.pdf', 'https://example.org/demo/nguyen-minh-anh-portfolio.pdf', FALSE),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb303', CURRENT_TIMESTAMP - INTERVAL '15' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb202', 'tran-gia-bao-cv.pdf', 'https://example.org/demo/tran-gia-bao-cv.pdf', TRUE),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb304', CURRENT_TIMESTAMP - INTERVAL '8' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb204', 'le-hoang-phuong-backend-cv.pdf', 'https://example.org/demo/le-hoang-phuong-backend-cv.pdf', TRUE),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb305', CURRENT_TIMESTAMP - INTERVAL '6' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb205', 'pham-thu-trang-marketing-cv.pdf', 'https://example.org/demo/pham-thu-trang-marketing-cv.pdf', TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO opportunities (id, created_at, updated_at, organization_id, category_id, title, description, requirements, location, remote, apply_url, deadline_at, status, view_count) VALUES
('cccccccc-cccc-cccc-cccc-cccccccccc01', CURRENT_TIMESTAMP - INTERVAL '14' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb101', '11111111-1111-1111-1111-111111111111', 'Java Backend Intern 2026', 'Join a product team building scalable services for regional customers.', 'Java fundamentals, SQL, Git, and willingness to learn Spring Boot.', 'Ho Chi Minh City', TRUE, NULL, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 'APPROVED', 248),
('cccccccc-cccc-cccc-cccc-cccccccccc02', CURRENT_TIMESTAMP - INTERVAL '10' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb101', '11111111-1111-1111-1111-111111111114', 'AI for Social Good Hackathon', 'Build a prototype that improves access to education or public services.', 'Team of 2-4 students, and a working demo is required.', 'Online', TRUE, 'https://example.org/apply/ai-hackathon', CURRENT_TIMESTAMP + INTERVAL '5' DAY, 'APPROVED', 531),
('cccccccc-cccc-cccc-cccc-cccccccccc03', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb101', '11111111-1111-1111-1111-111111111112', 'Junior Product Associate', 'Early-career product role working with engineering and design teams.', 'Strong communication and product thinking.', 'Ha Noi', FALSE, NULL, CURRENT_TIMESTAMP + INTERVAL '45' DAY, 'PENDING', 12),
('cccccccc-cccc-cccc-cccc-cccccccccc04', CURRENT_TIMESTAMP - INTERVAL '3' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb102', '11111111-1111-1111-1111-111111111115', 'Green Leaders Scholarship', 'Scholarship for students leading measurable sustainability initiatives.', 'Current university student with an active community project.', 'Vietnam', TRUE, 'https://example.org/apply/green-scholarship', CURRENT_TIMESTAMP + INTERVAL '60' DAY, 'DRAFT', 0),
('cccccccc-cccc-cccc-cccc-cccccccccc05', CURRENT_TIMESTAMP - INTERVAL '20' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb102', '11111111-1111-1111-1111-111111111113', 'Sustainable Campus Challenge', 'National case competition for practical low-carbon campus solutions.', 'Cross-disciplinary student teams are encouraged.', 'Da Nang', FALSE, NULL, CURRENT_TIMESTAMP + INTERVAL '20' DAY, 'REJECTED', 35),
('cccccccc-cccc-cccc-cccc-cccccccccc06', CURRENT_TIMESTAMP - INTERVAL '90' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb101', '11111111-1111-1111-1111-111111111111', 'Frontend Internship Spring Cohort', 'Completed internship cohort retained for history and dashboard metrics.', 'React and TypeScript fundamentals.', 'Ho Chi Minh City', FALSE, NULL, CURRENT_TIMESTAMP - INTERVAL '10' DAY, 'CLOSED', 410),
('cccccccc-cccc-cccc-cccc-cccccccccc07', CURRENT_TIMESTAMP - INTERVAL '7' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb102', '11111111-1111-1111-1111-111111111117', 'Climate Startup Incubator', 'Twelve-week incubation program with mentors and investor office hours.', 'Student-led startup with an MVP or validated problem.', 'Singapore / Remote', TRUE, 'https://example.org/apply/climate-incubator', CURRENT_TIMESTAMP + INTERVAL '90' DAY, 'APPROVED', 189),
('cccccccc-cccc-cccc-cccc-cccccccccc08', CURRENT_TIMESTAMP - INTERVAL '5' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb104', '11111111-1111-1111-1111-111111111111', 'Software Engineering Intern 2026', 'Work with VNG engineering teams on high-traffic consumer platforms and internal developer tooling.', 'Java or TypeScript, data structures, Git, and strong problem-solving skills.', 'Ho Chi Minh City', FALSE, NULL, CURRENT_TIMESTAMP + INTERVAL '40' DAY, 'APPROVED', 1260),
('cccccccc-cccc-cccc-cccc-cccccccccc09', CURRENT_TIMESTAMP - INTERVAL '4' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb104', '11111111-1111-1111-1111-111111111114', 'Zalo AI Challenge for Students', 'Build Vietnamese language and computer vision prototypes using an anonymized challenge dataset.', 'Teams of up to four students; submit source code, demo, and technical report.', 'Online', TRUE, 'https://example.org/apply/zalo-ai-challenge', CURRENT_TIMESTAMP + INTERVAL '18' DAY, 'APPROVED', 942),
('cccccccc-cccc-cccc-cccc-cccccccccc10', CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb104', '11111111-1111-1111-1111-111111111112', 'Associate Product Intern', 'Support product discovery, experiment analysis, and delivery coordination for a digital product team.', 'Product thinking, communication, spreadsheets, and basic analytics.', 'Ha Noi', TRUE, NULL, CURRENT_TIMESTAMP + INTERVAL '35' DAY, 'PENDING', 84),
('cccccccc-cccc-cccc-cccc-cccccccccc11', CURRENT_TIMESTAMP - INTERVAL '9' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb102', '11111111-1111-1111-1111-111111111116', 'Student Climate Venture Grant', 'Seed grants and coaching for student teams validating solutions in climate adaptation and circular economy.', 'Student-led team, validated user problem, six-month implementation plan, and measurable impact targets.', 'Vietnam', TRUE, 'https://example.org/apply/climate-grant', CURRENT_TIMESTAMP + INTERVAL '52' DAY, 'APPROVED', 377),
('cccccccc-cccc-cccc-cccc-cccccccccc12', CURRENT_TIMESTAMP - INTERVAL '16' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb101', '11111111-1111-1111-1111-111111111113', 'FPT Digital Innovation Case Competition', 'Solve a real enterprise transformation case and present a feasible implementation roadmap.', 'Cross-functional teams of three to five university students.', 'Da Nang', FALSE, NULL, CURRENT_TIMESTAMP + INTERVAL '12' DAY, 'APPROVED', 718)
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
('cccccccc-cccc-cccc-cccc-cccccccccc07', '22222222-2222-2222-2222-222222222225'),
('cccccccc-cccc-cccc-cccc-cccccccccc08', '22222222-2222-2222-2222-222222222222'),
('cccccccc-cccc-cccc-cccc-cccccccccc08', '22222222-2222-2222-2222-222222222223'),
('cccccccc-cccc-cccc-cccc-cccccccccc09', '22222222-2222-2222-2222-222222222221'),
('cccccccc-cccc-cccc-cccc-cccccccccc09', '22222222-2222-2222-2222-222222222225'),
('cccccccc-cccc-cccc-cccc-cccccccccc10', '22222222-2222-2222-2222-222222222224'),
('cccccccc-cccc-cccc-cccc-cccccccccc11', '22222222-2222-2222-2222-222222222221'),
('cccccccc-cccc-cccc-cccc-cccccccccc12', '22222222-2222-2222-2222-222222222223')
ON CONFLICT DO NOTHING;

INSERT INTO bookmarks (id, created_at, updated_at, student_id, opportunity_id) VALUES
('dddddddd-dddd-dddd-dddd-dddddddddd01', CURRENT_TIMESTAMP - INTERVAL '4' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', 'cccccccc-cccc-cccc-cccc-cccccccccc02'),
('dddddddd-dddd-dddd-dddd-dddddddddd02', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', 'cccccccc-cccc-cccc-cccc-cccccccccc07'),
('dddddddd-dddd-dddd-dddd-dddddddddd03', CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb202', 'cccccccc-cccc-cccc-cccc-cccccccccc01'),
('dddddddd-dddd-dddd-dddd-dddddddddd04', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb204', 'cccccccc-cccc-cccc-cccc-cccccccccc08'),
('dddddddd-dddd-dddd-dddd-dddddddddd05', CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb204', 'cccccccc-cccc-cccc-cccc-cccccccccc09'),
('dddddddd-dddd-dddd-dddd-dddddddddd06', CURRENT_TIMESTAMP - INTERVAL '3' DAY, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb205', 'cccccccc-cccc-cccc-cccc-cccccccccc11'),
('dddddddd-dddd-dddd-dddd-dddddddddd07', CURRENT_TIMESTAMP - INTERVAL '4' HOUR, CURRENT_TIMESTAMP, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb205', 'cccccccc-cccc-cccc-cccc-cccccccccc12')
ON CONFLICT DO NOTHING;

INSERT INTO applications (id, created_at, updated_at, student_id, opportunity_id, resume_id, cover_letter, status) VALUES
('dddddddd-dddd-dddd-dddd-dddddddddd11', CURRENT_TIMESTAMP - INTERVAL '12' DAY, CURRENT_TIMESTAMP - INTERVAL '12' DAY, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', 'cccccccc-cccc-cccc-cccc-cccccccccc01', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb301', 'I would like to contribute my Java and React experience while learning from the backend team.', 'APPLIED'),
('dddddddd-dddd-dddd-dddd-dddddddddd12', CURRENT_TIMESTAMP - INTERVAL '8' DAY, CURRENT_TIMESTAMP - INTERVAL '3' DAY, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb202', 'cccccccc-cccc-cccc-cccc-cccccccccc02', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb303', 'My analytics background can help the team validate impact with evidence.', 'REVIEWING'),
('dddddddd-dddd-dddd-dddd-dddddddddd13', CURRENT_TIMESTAMP - INTERVAL '25' DAY, CURRENT_TIMESTAMP - INTERVAL '5' DAY, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb201', 'cccccccc-cccc-cccc-cccc-cccccccccc07', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb301', 'I am building a student sustainability tool and would value the incubator mentorship.', 'ACCEPTED'),
('dddddddd-dddd-dddd-dddd-dddddddddd14', CURRENT_TIMESTAMP - INTERVAL '40' DAY, CURRENT_TIMESTAMP - INTERVAL '15' DAY, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb202', 'cccccccc-cccc-cccc-cccc-cccccccccc06', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb303', 'I am interested in applying analytics to frontend product decisions.', 'REJECTED'),
('dddddddd-dddd-dddd-dddd-dddddddddd15', CURRENT_TIMESTAMP - INTERVAL '4' DAY, CURRENT_TIMESTAMP - INTERVAL '2' DAY, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb204', 'cccccccc-cccc-cccc-cccc-cccccccccc08', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb304', 'I have built Spring Boot services and would like to contribute while learning production engineering practices.', 'REVIEWING'),
('dddddddd-dddd-dddd-dddd-dddddddddd16', CURRENT_TIMESTAMP - INTERVAL '3' DAY, CURRENT_TIMESTAMP - INTERVAL '3' DAY, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb205', 'cccccccc-cccc-cccc-cccc-cccccccccc11', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb305', 'My sustainability campaign experience can help grantees communicate impact to students and partners.', 'APPLIED'),
('dddddddd-dddd-dddd-dddd-dddddddddd17', CURRENT_TIMESTAMP - INTERVAL '7' DAY, CURRENT_TIMESTAMP - INTERVAL '1' DAY, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb202', 'cccccccc-cccc-cccc-cccc-cccccccccc12', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb303', 'I enjoy case competitions and can contribute data-backed market analysis to a cross-functional team.', 'ACCEPTED')
ON CONFLICT DO NOTHING;

INSERT INTO notifications (id, created_at, updated_at, user_id, type, title, message, read_at, action_url, dedupe_key) VALUES
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee01', CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'DEADLINE_REMINDER', 'Deadline approaching', 'AI for Social Good Hackathon closes in 5 days.', NULL, '/opportunities/cccccccc-cccc-cccc-cccc-cccccccccc02', 'demo-deadline-student-1'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee02', CURRENT_TIMESTAMP - INTERVAL '3' DAY, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'APPLICATION_STATUS', 'Application under review', 'Your application is now being reviewed.', NULL, '/student/applications', 'demo-application-reviewing-student-2'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee03', CURRENT_TIMESTAMP - INTERVAL '6' DAY, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa5', 'OPPORTUNITY_APPROVED', 'Opportunity approved', 'Java Backend Intern 2026 was approved and is now public.', CURRENT_TIMESTAMP - INTERVAL '5' DAY, '/organization/opportunities', 'demo-opportunity-approved-org-1'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee04', CURRENT_TIMESTAMP - INTERVAL '19' DAY, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa6', 'OPPORTUNITY_REJECTED', 'Opportunity needs changes', 'Sustainable Campus Challenge was rejected. Review the admin note.', NULL, '/organization/opportunities', 'demo-opportunity-rejected-org-2'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee05', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'WEEKLY_DIGEST', 'Your weekly opportunity digest', 'Three new opportunities match your interests.', CURRENT_TIMESTAMP - INTERVAL '1' DAY, '/explore', 'demo-weekly-digest-student-1'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee06', CURRENT_TIMESTAMP - INTERVAL '30' DAY, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'SYSTEM', 'Demo environment ready', 'Sample data is available for end-to-end testing.', CURRENT_TIMESTAMP - INTERVAL '29' DAY, '/admin/dashboard', 'demo-system-admin'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee07', CURRENT_TIMESTAMP - INTERVAL '2' HOUR, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa8', 'APPLICATION_STATUS', 'Application is under review', 'VNG Campus Recruitment is reviewing your Software Engineering Intern application.', NULL, '/student/applications', 'demo-review-student-3'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee08', CURRENT_TIMESTAMP - INTERVAL '8' HOUR, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa0', 'DEADLINE_REMINDER', 'Competition deadline approaching', 'FPT Digital Innovation Case Competition closes in 12 days.', NULL, '/opportunities/cccccccc-cccc-cccc-cccc-cccccccccc12', 'demo-deadline-student-4'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeee09', CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa9', 'SYSTEM', 'New candidate applications', 'Two new student applications are ready for review.', NULL, '/organization/applicants', 'demo-new-applications-org-3')
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

COMMIT;

SELECT role, status, COUNT(*) AS account_count
FROM users
GROUP BY role, status
ORDER BY role, status;

SELECT status, COUNT(*) AS opportunity_count
FROM opportunities
GROUP BY status
ORDER BY status;

SELECT status, COUNT(*) AS application_count
FROM applications
GROUP BY status
ORDER BY status;

SELECT status, COUNT(*) AS report_count
FROM reports
GROUP BY status
ORDER BY status;
