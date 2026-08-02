UPDATE opportunity_categories
SET name = CASE slug
        WHEN 'internship' THEN 'Thực tập'
        WHEN 'startup-recruitment' THEN 'Tuyển dụng khởi nghiệp'
        WHEN 'competition' THEN 'Cuộc thi'
        WHEN 'hackathon' THEN 'Hackathon'
        WHEN 'scholarship' THEN 'Học bổng'
        WHEN 'fund' THEN 'Quỹ hỗ trợ'
        WHEN 'incubator' THEN 'Ươm tạo'
    END,
    description = CASE slug
        WHEN 'internship' THEN 'Chương trình thực tập và đào tạo dành cho sinh viên'
        WHEN 'startup-recruitment' THEN 'Việc làm tại startup, tìm đồng sáng lập và tuyển thành viên giai đoạn đầu'
        WHEN 'competition' THEN 'Các cuộc thi đổi mới sáng tạo và cuộc thi dành cho sinh viên'
        WHEN 'hackathon' THEN 'Sự kiện lập trình, phát triển sản phẩm nhanh và thử thách công nghệ'
        WHEN 'scholarship' THEN 'Học bổng, tài trợ và các cơ hội hỗ trợ tài chính'
        WHEN 'fund' THEN 'Quỹ khởi nghiệp sinh viên và các chương trình đầu tư'
        WHEN 'incubator' THEN 'Các chương trình ươm tạo và tăng tốc khởi nghiệp'
    END,
    updated_at = CURRENT_TIMESTAMP
WHERE slug IN ('internship', 'startup-recruitment', 'competition', 'hackathon', 'scholarship', 'fund', 'incubator');

UPDATE tags
SET name = CASE slug
        WHEN 'remote' THEN 'Từ xa'
        WHEN 'paid' THEN 'Có trả lương'
        WHEN 'beginner-friendly' THEN 'Phù hợp người mới'
        WHEN 'startup' THEN 'Khởi nghiệp'
        WHEN 'international' THEN 'Quốc tế'
    END,
    updated_at = CURRENT_TIMESTAMP
WHERE slug IN ('remote', 'paid', 'beginner-friendly', 'startup', 'international');

UPDATE skills
SET name = CASE slug
        WHEN 'product-management' THEN 'Quản lý sản phẩm'
        WHEN 'ui-ux-design' THEN 'Thiết kế UI/UX'
        WHEN 'data-analysis' THEN 'Phân tích dữ liệu'
    END,
    updated_at = CURRENT_TIMESTAMP
WHERE slug IN ('product-management', 'ui-ux-design', 'data-analysis');
