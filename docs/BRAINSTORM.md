# Opportunity Board - Brainstorm MVP

## 1. Định hướng sản phẩm

**Opportunity Board** là nền tảng web giúp sinh viên tìm kiếm, lưu, theo dõi và ứng tuyển các cơ hội như:

- Thực tập
- Tuyển dụng startup
- Hackathon
- Cuộc thi đổi mới sáng tạo
- Học bổng
- Quỹ đầu tư
- Chương trình ươm tạo
- Chương trình accelerator

MVP tập trung vào lời hứa cốt lõi:

> Giúp sinh viên tìm đúng cơ hội, ứng tuyển đúng hạn, và quản lý tiến trình trong một nơi duy nhất.

MVP **không tích hợp AI** ở giai đoạn đầu.

---

## 2. Phạm vi MVP đã chốt

### Vai trò người dùng

| Vai trò | Mục đích |
|---|---|
| Sinh viên | Tìm, lưu, ứng tuyển và theo dõi cơ hội |
| Tổ chức | Đăng cơ hội, quản lý bài đăng và ứng viên |
| Admin | Kiểm duyệt bài đăng, quản lý người dùng và xử lý báo cáo |

---

## 3. Tính năng chính

### Must Have

| Tính năng | Mô tả |
|---|---|
| Đăng ký/đăng nhập | Email/password |
| Phân quyền | Student, Organization, Admin |
| Hồ sơ sinh viên | Trường, ngành, năm học, kỹ năng, sở thích |
| Hồ sơ tổ chức | Tên, logo, mô tả, website |
| Danh sách cơ hội | Hiển thị các cơ hội đang mở |
| Tạo cơ hội | Tổ chức tạo bài đăng |
| Kiểm duyệt cơ hội | Admin duyệt trước khi public |
| Trang chi tiết cơ hội | Mô tả, điều kiện, quyền lợi, deadline |
| Tìm kiếm | Theo tiêu đề, tổ chức, mô tả |
| Bộ lọc | Loại cơ hội, lĩnh vực, địa điểm, deadline |
| Lưu cơ hội | Sinh viên bookmark cơ hội |
| Ứng tuyển | Form nội bộ hoặc link ngoài |
| Theo dõi trạng thái | Saved, Applied, Reviewing, Accepted, Rejected |
| Email notification | Xác nhận, nhắc deadline, cập nhật trạng thái |
| Dashboard theo vai trò | Student, Organization, Admin |
| Swagger/OpenAPI | Tài liệu API cho frontend tích hợp |

### Should Have

| Tính năng | Mô tả |
|---|---|
| Upload CV | Lưu CV bằng Cloudinary |
| Quản lý ứng viên | Tổ chức xem sinh viên ứng tuyển |
| Thống kê cơ bản | Lượt xem, lượt lưu, lượt ứng tuyển |
| Nhắc deadline tự động | Spring Scheduler hoặc Quartz |
| Báo cáo bài đăng | Sinh viên report cơ hội sai hoặc đáng ngờ |
| Weekly digest | Email tổng hợp cơ hội mới |
| Gợi ý cơ hội thủ công | Rule-based, không dùng AI |
| Bộ sưu tập cơ hội | Admin tạo collection theo chủ đề |

---

## 4. User Journey

### Sinh viên

1. Đăng ký tài khoản.
2. Đăng nhập.
3. Hoàn thiện hồ sơ: trường, ngành, năm học, kỹ năng, sở thích.
4. Upload CV nếu muốn ứng tuyển nhanh.
5. Vào trang Explore.
6. Tìm kiếm hoặc lọc cơ hội theo loại, lĩnh vực, địa điểm, deadline.
7. Mở trang chi tiết cơ hội.
8. Xem mô tả, điều kiện, quyền lợi, deadline.
9. Lưu cơ hội hoặc ứng tuyển.
10. Theo dõi trạng thái trong Application Tracker.
11. Nhận email nhắc deadline hoặc cập nhật trạng thái.
12. Hoàn tất quá trình ứng tuyển.

### Tổ chức

1. Đăng ký tài khoản tổ chức.
2. Tạo hồ sơ tổ chức.
3. Tạo bài đăng cơ hội.
4. Gửi bài đăng cho admin duyệt.
5. Sau khi được duyệt, bài đăng xuất hiện công khai.
6. Theo dõi lượt xem, lượt lưu, lượt ứng tuyển.
7. Xem danh sách ứng viên.
8. Cập nhật trạng thái ứng tuyển.

### Admin

1. Đăng nhập.
2. Xem bài đăng chờ duyệt.
3. Duyệt hoặc từ chối bài đăng.
4. Xử lý báo cáo từ sinh viên.
5. Quản lý tài khoản sinh viên và tổ chức.
6. Theo dõi số liệu hệ thống.

---

## 5. Dashboard

### Student Dashboard

| Thành phần | Nội dung |
|---|---|
| Cơ hội đề xuất | Gợi ý theo ngành, kỹ năng, danh mục quan tâm |
| Sắp hết hạn | Cơ hội đã lưu hoặc phù hợp sắp đến deadline |
| Cơ hội đã lưu | Danh sách bookmark |
| Application Tracker | Theo dõi Saved, Applied, Reviewing, Accepted, Rejected |
| Hồ sơ của tôi | Mức độ hoàn thiện hồ sơ |
| Cơ hội mới | Bài đăng mới nhất |
| Lịch deadline | Các mốc quan trọng sắp tới |

### Organization Dashboard

| Thành phần | Nội dung |
|---|---|
| Bài đăng của tôi | Danh sách cơ hội đã tạo |
| Chờ duyệt | Bài đăng đang chờ admin duyệt |
| Đang hoạt động | Cơ hội đã public |
| Ứng viên | Danh sách sinh viên ứng tuyển |
| Thống kê cơ bản | Lượt xem, lưu, ứng tuyển |
| Hồ sơ tổ chức | Thông tin tổ chức |

### Admin Dashboard

| Thành phần | Nội dung |
|---|---|
| Bài chờ duyệt | Duyệt/từ chối cơ hội |
| Báo cáo | Xử lý bài bị report |
| Quản lý người dùng | Sinh viên, tổ chức |
| Thống kê hệ thống | Tổng user, cơ hội, ứng tuyển |
| Quản lý danh mục | Category, tag, field |

---

## 6. Opportunity Detail Page

### Thông tin cần có

| Trường | Mô tả |
|---|---|
| Title | Tên cơ hội |
| Organization | Tổ chức đăng |
| Category | Internship, Scholarship, Hackathon, Competition, Startup Job, Funding, Incubator |
| Short Description | Mô tả ngắn |
| Full Description | Nội dung chi tiết |
| Requirements | Yêu cầu ứng tuyển |
| Eligibility | Điều kiện tham gia |
| Benefits | Lương, học bổng, giải thưởng, mentorship |
| Location | Remote, Hybrid, On-site |
| Deadline | Hạn ứng tuyển |
| Start Date | Ngày bắt đầu |
| End Date | Ngày kết thúc nếu có |
| Application Method | Internal Form hoặc External Link |
| Skills | Kỹ năng liên quan |
| Field | Lĩnh vực |
| Status | Draft, Pending, Approved, Rejected, Closed |
| Created By | Tổ chức tạo bài |
| View Count | Lượt xem |
| Save Count | Lượt lưu |
| Apply Count | Lượt ứng tuyển |

### Hành động người dùng

| Vai trò | Hành động |
|---|---|
| Sinh viên | Lưu, ứng tuyển, chia sẻ, báo cáo |
| Tổ chức | Sửa, đóng bài, xem ứng viên |
| Admin | Duyệt, từ chối, gỡ, xử lý báo cáo |

---

## 7. Search & Discovery

### Search và Filter

| Tính năng | Mô tả |
|---|---|
| Keyword Search | Tìm theo tiêu đề, tổ chức, mô tả |
| Category Filter | Lọc theo internship, scholarship, hackathon, competition |
| Location Filter | Remote, Hybrid, On-site, thành phố |
| Deadline Filter | Sắp hết hạn, tuần này, tháng này |
| Field Filter | Technology, Business, Design, Finance, Marketing |
| Skill Filter | Java, React, Data, UI/UX |
| Organization Filter | Lọc theo tổ chức đăng |
| Status Filter | Đang mở, sắp hết hạn |
| Sort | Mới nhất, deadline gần nhất, phổ biến nhất |

### Recommendation không dùng AI

Hệ thống có thể gợi ý cơ hội bằng logic rule-based:

- Trùng ngành học của sinh viên
- Trùng kỹ năng sinh viên đã khai báo
- Trùng danh mục sinh viên quan tâm
- Cơ hội còn hạn ứng tuyển
- Cơ hội được nhiều sinh viên lưu
- Cơ hội mới đăng trong 7 ngày gần nhất

---

## 8. Gamification

| Tính năng | Mục tiêu |
|---|---|
| Profile Completion Score | Khuyến khích hoàn thiện hồ sơ |
| Application Goal | Đặt mục tiêu ứng tuyển mỗi tháng |
| Saved Opportunity Reminder | Nhắc quay lại xử lý cơ hội đã lưu |
| Daily Check-in | Khuyến khích xem cơ hội mới mỗi ngày |
| Badges | Ghi nhận hành vi tích cực |
| Weekly Progress | Tổng kết số cơ hội đã xem, lưu, ứng tuyển |
| Deadline Streak | Không bỏ lỡ deadline đã lưu |

Gamification nên nhẹ, chuyên nghiệp, không làm nền tảng giống game quá mức.

---

## 9. UI/UX

### Phong cách tổng thể

- Sạch, hiện đại, chuyên nghiệp
- Dễ đọc, dễ lọc, dễ thao tác
- Giống dashboard công việc
- Tập trung vào tốc độ tìm kiếm và quyết định
- Không quá màu mè

### Cảm hứng thiết kế

| Nguồn cảm hứng | Ứng dụng |
|---|---|
| LinkedIn | Hồ sơ, tổ chức, bài đăng cơ hội |
| Notion | Layout sạch, database view, tag |
| Product Hunt | Trending, newest, featured |
| GitHub | Profile, activity, project links |

### Navigation sinh viên

- Dashboard
- Explore
- Saved
- Applications
- Calendar
- Profile

### Navigation tổ chức

- Dashboard
- Opportunities
- Applicants
- Analytics
- Organization Profile
- Settings

### Navigation admin

- Dashboard
- Pending Opportunities
- Reports
- Users
- Organizations
- Categories

---

## 10. Database Design

| Entity | Mô tả |
|---|---|
| User | Tài khoản chung |
| Role | STUDENT, ORGANIZATION, ADMIN |
| StudentProfile | Hồ sơ sinh viên |
| OrganizationProfile | Hồ sơ tổ chức |
| Opportunity | Bài đăng cơ hội |
| OpportunityCategory | Loại cơ hội |
| Tag | Tag kỹ năng/lĩnh vực |
| OpportunityTag | Quan hệ nhiều-nhiều giữa Opportunity và Tag |
| Application | Đơn ứng tuyển |
| Bookmark | Cơ hội đã lưu |
| Resume | CV của sinh viên |
| Notification | Thông báo |
| Report | Báo cáo bài đăng |
| AdminReview | Lịch sử duyệt bài |
| Skill | Kỹ năng |
| StudentSkill | Quan hệ sinh viên-kỹ năng |

### Quan hệ chính

| Quan hệ | Kiểu |
|---|---|
| User - StudentProfile | One-to-One |
| User - OrganizationProfile | One-to-One |
| OrganizationProfile - Opportunity | One-to-Many |
| StudentProfile - Application | One-to-Many |
| Opportunity - Application | One-to-Many |
| StudentProfile - Bookmark | One-to-Many |
| Opportunity - Bookmark | One-to-Many |
| Opportunity - Tag | Many-to-Many |
| StudentProfile - Skill | Many-to-Many |
| Opportunity - AdminReview | One-to-Many |
| Opportunity - Report | One-to-Many |
| User - Notification | One-to-Many |

---

## 11. REST APIs cho MVP

### Auth APIs

| Method | Endpoint | Mục đích |
|---|---|---|
| POST | `/api/auth/register` | Đăng ký |
| POST | `/api/auth/login` | Đăng nhập |
| POST | `/api/auth/logout` | Đăng xuất |
| GET | `/api/auth/me` | Lấy user hiện tại |
| POST | `/api/auth/refresh-token` | Làm mới JWT |

### Student APIs

| Method | Endpoint | Mục đích |
|---|---|---|
| GET | `/api/students/me` | Xem hồ sơ |
| PATCH | `/api/students/me` | Cập nhật hồ sơ |
| POST | `/api/students/me/resume` | Upload CV |
| GET | `/api/students/me/dashboard` | Dashboard sinh viên |

### Organization APIs

| Method | Endpoint | Mục đích |
|---|---|---|
| GET | `/api/organizations/me` | Xem hồ sơ tổ chức |
| PATCH | `/api/organizations/me` | Cập nhật hồ sơ |
| GET | `/api/organizations/me/dashboard` | Dashboard tổ chức |
| GET | `/api/organizations/me/opportunities` | Danh sách bài đăng |
| GET | `/api/organizations/me/applications` | Danh sách ứng viên |

### Opportunity APIs

| Method | Endpoint | Mục đích |
|---|---|---|
| GET | `/api/opportunities` | Danh sách/tìm kiếm/lọc |
| POST | `/api/opportunities` | Tạo bài đăng |
| GET | `/api/opportunities/{id}` | Chi tiết bài đăng |
| PATCH | `/api/opportunities/{id}` | Cập nhật bài đăng |
| DELETE | `/api/opportunities/{id}` | Xóa/đóng bài đăng |
| POST | `/api/opportunities/{id}/bookmark` | Lưu cơ hội |
| DELETE | `/api/opportunities/{id}/bookmark` | Bỏ lưu |
| POST | `/api/opportunities/{id}/apply` | Ứng tuyển |

### Application APIs

| Method | Endpoint | Mục đích |
|---|---|---|
| GET | `/api/applications/me` | Đơn ứng tuyển của sinh viên |
| GET | `/api/applications/{id}` | Chi tiết đơn ứng tuyển |
| PATCH | `/api/applications/{id}/status` | Cập nhật trạng thái |

### Admin APIs

| Method | Endpoint | Mục đích |
|---|---|---|
| GET | `/api/admin/opportunities/pending` | Danh sách bài chờ duyệt |
| POST | `/api/admin/opportunities/{id}/approve` | Duyệt bài |
| POST | `/api/admin/opportunities/{id}/reject` | Từ chối bài |
| GET | `/api/admin/reports` | Danh sách báo cáo |
| PATCH | `/api/admin/users/{id}/status` | Khóa/mở tài khoản |
| GET | `/api/admin/dashboard` | Dashboard admin |

---

## 12. Tech Stack đề xuất

| Thành phần | Công nghệ | Lý do |
|---|---|---|
| Frontend | React + TypeScript + Vite | Khởi tạo nhanh, hiệu năng cao, phát triển SPA hiện đại |
| UI | Tailwind CSS + shadcn/ui | Giao diện đẹp, responsive, dễ tùy chỉnh và xây dựng MVP |
| Backend | Spring Boot 3 | Framework Java mạnh mẽ, phù hợp phát triển REST API, bảo mật và dễ mở rộng |
| Build Tool | Maven | Quản lý dependency và build dự án Spring Boot |
| Database | PostgreSQL | Hệ quản trị CSDL quan hệ ổn định, phù hợp dữ liệu của Opportunity Board |
| ORM | Spring Data JPA + Hibernate | Giảm lượng code thao tác database, hỗ trợ mapping entity tốt |
| Authentication | Spring Security + JWT | Xác thực và phân quyền an toàn |
| Search | PostgreSQL Full-Text Search | Đủ cho MVP, có thể nâng cấp sau |
| Storage | Cloudinary | Lưu CV, logo doanh nghiệp và tệp đính kèm |
| Email | Spring Mail SMTP | Gửi email xác nhận, thông báo, nhắc hạn |
| Background Jobs | Spring Scheduler hoặc Quartz | Tự động gửi email và cập nhật trạng thái |
| Documentation | Swagger / OpenAPI | Sinh tài liệu API tự động |
| Testing | JUnit 5 + Mockito | Kiểm thử backend |
| Deployment | Vercel + Render/Railway + Neon PostgreSQL | Dễ triển khai frontend, backend và database |

---

## 13. Monetization

| Mô hình | Mô tả |
|---|---|
| Featured Opportunities | Tổ chức trả phí để ghim cơ hội nổi bật |
| Organization Subscription | Gói trả phí cho tổ chức đăng nhiều bài và xem analytics |
| University Partnership | Trường trả phí để có bảng cơ hội riêng cho sinh viên |
| Sponsored Collections | Bộ sưu tập cơ hội được tài trợ |
| Recruitment Package | Hỗ trợ doanh nghiệp tiếp cận sinh viên phù hợp |

Mô hình ban đầu hợp lý:

- Miễn phí cho sinh viên.
- Tổ chức được đăng một số bài miễn phí.
- Thu phí với bài nổi bật, analytics và số lượng bài đăng lớn hơn.

---

## 14. Future Expansion

### Giai đoạn 1: MVP Discovery Board

- Đăng nhập/phân quyền
- Hồ sơ sinh viên/tổ chức
- Đăng và duyệt cơ hội
- Tìm kiếm/lọc
- Bookmark
- Ứng tuyển
- Email nhắc deadline

### Giai đoạn 2: Application Management

- Application tracker nâng cao
- Ghi chú cá nhân cho từng cơ hội
- Calendar tích hợp deadline
- Export danh sách ứng tuyển
- Lịch sử ứng tuyển

### Giai đoạn 3: Student Career Hub

- Portfolio sinh viên
- Project showcase
- Career roadmap thủ công
- Mentor review
- Hồ sơ công khai cho sinh viên

### Giai đoạn 4: University Career Network

- Trang riêng cho từng trường
- Admin dashboard cho career center
- Báo cáo xu hướng sinh viên
- Feed cơ hội theo ngành/khoa

### Giai đoạn 5: Talent & Startup Ecosystem

- Nhà tuyển dụng tìm sinh viên
- Startup tìm co-founder
- Nhà đầu tư tìm dự án sinh viên
- Vườn ươm tuyển đội nhóm
- Hackathon và innovation challenge tích hợp trực tiếp

---

## 15. Kết luận hướng MVP

Opportunity Board phiên bản MVP sẽ là:

> Một nền tảng khám phá cơ hội, lưu cơ hội, ứng tuyển và theo dõi deadline cho sinh viên, không tích hợp AI ở giai đoạn đầu.

Hướng này giúp sản phẩm dễ xây dựng hơn, backend rõ ràng hơn, chi phí vận hành thấp hơn và vẫn đủ giá trị thực tế để triển khai thử nghiệm trong môi trường đại học.
