-- Local/demo seed accounts. Change these passwords outside local development.
-- admin@opportunityboard.local / Admin@123
-- organization@opportunityboard.local / Admin@123

INSERT INTO users (
    id, created_at, updated_at, email, password_hash, full_name, role, status,
    email_verified_at, email_verification_token, email_verification_token_expires_at
) VALUES
(
    '80000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
    'admin@opportunityboard.local', '$2a$10$rhgS8NF8.Z/OKFlB2SltvemhL9be.Y7EC6qo1ZOktszVvER1HBxE6',
    'Opportunity Board Admin', 'ADMIN', 'ACTIVE', CURRENT_TIMESTAMP, NULL, NULL
),
(
    '80000000-0000-0000-0000-000000000002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
    'organization@opportunityboard.local', '$2a$10$rhgS8NF8.Z/OKFlB2SltvemhL9be.Y7EC6qo1ZOktszVvER1HBxE6',
    'Future Hub Vietnam', 'ORGANIZATION', 'ACTIVE', CURRENT_TIMESTAMP, NULL, NULL
) ON CONFLICT DO NOTHING;

INSERT INTO organization_profiles (
    id, created_at, updated_at, user_id, organization_name, industry,
    website_url, logo_url, description, verification_status, verified_at, verified_by
) VALUES (
    '81000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
    '80000000-0000-0000-0000-000000000002', 'Future Hub Vietnam',
    'Education Technology', 'https://example.com/future-hub', NULL,
    'Cộng đồng kết nối sinh viên với doanh nghiệp, học bổng và các chương trình đổi mới sáng tạo.',
    'VERIFIED', CURRENT_TIMESTAMP, '80000000-0000-0000-0000-000000000001'
) ON CONFLICT DO NOTHING;

INSERT INTO opportunities (
    id, created_at, updated_at, organization_id, category_id, title, description,
    requirements, location, remote, apply_url, deadline_at, status, view_count
) VALUES
(
    '82000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP - INTERVAL '5' DAY, CURRENT_TIMESTAMP,
    '81000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111',
    'Thực tập sinh Backend Java 2026',
    'Tham gia phát triển REST API cho nền tảng giáo dục, làm việc cùng mentor và trải nghiệm quy trình phát triển sản phẩm thực tế.',
    'Sinh viên CNTT; nắm Java Core, Spring Boot và SQL; có tinh thần học hỏi và làm việc nhóm.',
    'TP. Hồ Chí Minh', TRUE, 'https://example.com/apply/backend-java', CURRENT_TIMESTAMP + INTERVAL '30' DAY, 'APPROVED', 148
),
(
    '82000000-0000-0000-0000-000000000002', CURRENT_TIMESTAMP - INTERVAL '4' DAY, CURRENT_TIMESTAMP,
    '81000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111',
    'Product Design Internship',
    'Thiết kế trải nghiệm người dùng cho sản phẩm hướng nghiệp dành cho sinh viên từ nghiên cứu đến prototype.',
    'Có portfolio UI/UX; sử dụng Figma; tư duy hệ thống và khả năng trình bày ý tưởng.',
    'Hà Nội', FALSE, 'https://example.com/apply/product-design', CURRENT_TIMESTAMP + INTERVAL '24' DAY, 'APPROVED', 96
),
(
    '82000000-0000-0000-0000-000000000003', CURRENT_TIMESTAMP - INTERVAL '3' DAY, CURRENT_TIMESTAMP,
    '81000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111115',
    'Học bổng Nhà kiến tạo tương lai',
    'Học bổng hỗ trợ học phí và mentoring cho sinh viên có dự án tạo tác động tích cực cho cộng đồng.',
    'GPA từ 7.5; có dự án cá nhân hoặc hoạt động cộng đồng; nộp bài luận và kế hoạch phát triển.',
    'Toàn quốc', TRUE, 'https://example.com/apply/future-maker', CURRENT_TIMESTAMP + INTERVAL '45' DAY, 'APPROVED', 231
),
(
    '82000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP,
    '81000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111114',
    'GreenTech Campus Hackathon 2026',
    'Cuộc thi 48 giờ xây dựng giải pháp công nghệ cho môi trường, năng lượng và khuôn viên đại học bền vững.',
    'Đội từ 3 đến 5 sinh viên; chấp nhận mọi chuyên ngành; có prototype vào cuối chương trình.',
    'Đà Nẵng', FALSE, 'https://example.com/apply/greentech-hackathon', CURRENT_TIMESTAMP + INTERVAL '18' DAY, 'APPROVED', 314
),
(
    '82000000-0000-0000-0000-000000000005', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP,
    '81000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111113',
    'Data Storytelling Challenge',
    'Phân tích bộ dữ liệu mở và kể một câu chuyện có giá trị bằng dashboard hoặc sản phẩm dữ liệu trực quan.',
    'Biết Excel, SQL, Python hoặc công cụ BI; đăng ký cá nhân hoặc đội tối đa 3 thành viên.',
    'Trực tuyến', TRUE, 'https://example.com/apply/data-storytelling', CURRENT_TIMESTAMP + INTERVAL '21' DAY, 'APPROVED', 175
),
(
    '82000000-0000-0000-0000-000000000006', CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP,
    '81000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111117',
    'Student Founder Launchpad',
    'Chương trình ươm tạo 10 tuần dành cho nhóm sinh viên đang phát triển ý tưởng startup giai đoạn đầu.',
    'Nhóm 2 đến 5 thành viên; có vấn đề rõ ràng, giải pháp ban đầu và cam kết tham gia đầy đủ.',
    'TP. Hồ Chí Minh', TRUE, 'https://example.com/apply/founder-launchpad', CURRENT_TIMESTAMP + INTERVAL '35' DAY, 'APPROVED', 122
),
(
    '82000000-0000-0000-0000-000000000007', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
    '81000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111112',
    'Frontend Developer cho startup EdTech',
    'Cơ hội tham gia đội ngũ sản phẩm giai đoạn sớm, xây dựng giao diện học tập và hệ thống quản trị nội dung.',
    'React, TypeScript, REST API; ưu tiên ứng viên có sản phẩm cá nhân hoặc đóng góp mã nguồn mở.',
    'Hà Nội', TRUE, NULL, CURRENT_TIMESTAMP + INTERVAL '28' DAY, 'PENDING', 0
),
(
    '82000000-0000-0000-0000-000000000008', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
    '81000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111116',
    'Quỹ hỗ trợ dự án sinh viên đổi mới sáng tạo',
    'Khoản tài trợ thử nghiệm dành cho dự án sinh viên giải quyết vấn đề thực tế tại trường đại học.',
    'Có đội ngũ sinh viên, bản mô tả vấn đề, kế hoạch thử nghiệm và ngân sách dự kiến.',
    'Toàn quốc', TRUE, NULL, CURRENT_TIMESTAMP + INTERVAL '40' DAY, 'PENDING', 0
) ON CONFLICT DO NOTHING;

INSERT INTO opportunity_tags (opportunity_id, tag_id) VALUES
('82000000-0000-0000-0000-000000000001', '22222222-2222-2222-2222-222222222221'),
('82000000-0000-0000-0000-000000000001', '22222222-2222-2222-2222-222222222222'),
('82000000-0000-0000-0000-000000000002', '22222222-2222-2222-2222-222222222223'),
('82000000-0000-0000-0000-000000000003', '22222222-2222-2222-2222-222222222225'),
('82000000-0000-0000-0000-000000000004', '22222222-2222-2222-2222-222222222223'),
('82000000-0000-0000-0000-000000000005', '22222222-2222-2222-2222-222222222221'),
('82000000-0000-0000-0000-000000000006', '22222222-2222-2222-2222-222222222224'),
('82000000-0000-0000-0000-000000000007', '22222222-2222-2222-2222-222222222221'),
('82000000-0000-0000-0000-000000000007', '22222222-2222-2222-2222-222222222224'),
('82000000-0000-0000-0000-000000000008', '22222222-2222-2222-2222-222222222223')
ON CONFLICT DO NOTHING;
