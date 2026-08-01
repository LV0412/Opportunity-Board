# Opportunity Board - Kế hoạch triển khai MVP

## Overview

Kế hoạch này chuyển nội dung đã chốt trong `docs/BRAINSTORM.md` thành một lộ trình triển khai cụ thể cho MVP của **Opportunity Board**.

MVP cần tạo một nền tảng web cho 3 vai trò:

- **Sinh viên**: tìm kiếm, lọc, lưu, ứng tuyển và theo dõi cơ hội.
- **Tổ chức**: tạo hồ sơ, đăng cơ hội, xem ứng viên và cập nhật trạng thái ứng tuyển.
- **Admin**: kiểm duyệt bài đăng, xử lý báo cáo và quản lý người dùng.

Hướng kỹ thuật đã chốt:

| Thành phần | Công nghệ |
|---|---|
| Frontend | React + TypeScript + Vite |
| UI | Tailwind CSS + shadcn/ui |
| Backend | Spring Boot 3 |
| Build Tool | Maven |
| Database | PostgreSQL |
| ORM | Spring Data JPA + Hibernate |
| Authentication | Spring Security + JWT |
| Search | PostgreSQL Full-Text Search |
| Storage | Cloudinary |
| Email | Spring Mail SMTP |
| Background Jobs | Spring Scheduler hoặc Quartz |
| Documentation | Swagger / OpenAPI |
| Testing | JUnit 5 + Mockito |
| Deployment | Vercel + Render/Railway + Neon PostgreSQL |

---

## Scope

### Trong phạm vi MVP

- Đăng ký, đăng nhập, JWT authentication.
- Phân quyền `STUDENT`, `ORGANIZATION`, `ADMIN`.
- Hồ sơ sinh viên.
- Hồ sơ tổ chức.
- CRUD cơ hội cho tổ chức.
- Admin duyệt hoặc từ chối cơ hội trước khi public.
- Danh sách cơ hội public.
- Trang chi tiết cơ hội.
- Tìm kiếm, lọc và sắp xếp cơ hội.
- Bookmark cơ hội.
- Ứng tuyển cơ hội.
- Theo dõi trạng thái ứng tuyển.
- Báo cáo bài đăng.
- Email thông báo và nhắc deadline.
- Dashboard cơ bản cho 3 vai trò.
- Swagger/OpenAPI cho backend.
- Unit test và integration test cho các service quan trọng.

### Ngoài phạm vi MVP

- Không tích hợp AI.
- Không xây dựng mạng xã hội.
- Không xây dựng LMS hoặc khóa học.
- Không xây dựng hệ thống ATS phức tạp.
- Không xây dựng marketplace tuyển dụng nâng cao.
- Không xây dựng app mobile native.

---

## Cấu trúc dự án đề xuất

Repo được chia thành 2 phần chính: `frontend/` cho React SPA và `backend/` cho Spring Boot REST API. Tài liệu sản phẩm và kế hoạch nằm trong `docs/`.

```text
Opportunity Board/
  docs/
    BRAINSTORM.md
    PLAN.md
  backend/
    pom.xml
    src/main/java/...
    src/main/resources/...
    src/test/java/...
  frontend/
    package.json
    vite.config.ts
    src/...
```

### Frontend: Hybrid Feature-based Structure

Frontend dùng cấu trúc kết hợp:

- `features/` chứa UI, hooks, API client và logic riêng theo từng tính năng.
- `pages/` chỉ đóng vai trò lắp ghép layout và feature để tạo màn hình hoàn chỉnh.
- `components/`, `hooks/`, `utils/`, `config/` chứa phần dùng chung toàn hệ thống.

```text
frontend/
  package.json
  vite.config.ts
  tsconfig.json
  tailwind.config.js
  src/
    assets/
    components/
      ui/
      common/
      forms/
      navigation/
    config/
      apiClient.ts
      constants.ts
      routes.ts
    context/
      AuthContext.tsx
    features/
      auth/
        components/
        hooks/
        api/
        types.ts
      opportunities/
        components/
        hooks/
        api/
        types.ts
      bookmarks/
        components/
        hooks/
        api/
        types.ts
      applications/
        components/
        hooks/
        api/
        types.ts
      students/
        components/
        hooks/
        api/
        types.ts
      organizations/
        components/
        hooks/
        api/
        types.ts
      admin/
        components/
        hooks/
        api/
        types.ts
      notifications/
        components/
        hooks/
        api/
        types.ts
    hooks/
    layouts/
      AuthLayout.tsx
      DashboardLayout.tsx
      AdminLayout.tsx
    pages/
      public/
      auth/
      student/
      organization/
      admin/
      NotFoundPage.tsx
    routes/
      AppRoutes.tsx
      ProtectedRoute.tsx
      RoleBasedRoute.tsx
    services/
      cloudinaryUpload.ts
      analytics.ts
    store/
    types/
    utils/
```

### Backend: Layered Spring Boot Structure

Backend dùng cấu trúc phân tầng rõ ràng. Controller nhận HTTP request, service xử lý nghiệp vụ, repository giao tiếp database, entity ánh xạ ORM, DTO chuẩn hóa request/response, infrastructure chứa tích hợp bên thứ ba.

Package gốc đề xuất: `com.opportunityboard`.

```text
backend/
  pom.xml
  src/main/java/com/opportunityboard/
    OpportunityBoardApplication.java
    config/
      SecurityConfig.java
      OpenApiConfig.java
      CorsConfig.java
      SchedulerConfig.java
    controller/
      AuthController.java
      StudentController.java
      OrganizationController.java
      OpportunityController.java
      BookmarkController.java
      ApplicationController.java
      ReportController.java
      AdminController.java
      DashboardController.java
    service/
      auth/
        AuthService.java
        impl/
          AuthServiceImpl.java
      user/
        UserService.java
        impl/
          UserServiceImpl.java
      student/
        StudentService.java
        impl/
          StudentServiceImpl.java
      organization/
        OrganizationService.java
        impl/
          OrganizationServiceImpl.java
      opportunity/
        OpportunityService.java
        OpportunitySearchService.java
        impl/
          OpportunityServiceImpl.java
          OpportunitySearchServiceImpl.java
      bookmark/
        BookmarkService.java
        impl/
          BookmarkServiceImpl.java
      application/
        ApplicationService.java
        impl/
          ApplicationServiceImpl.java
      report/
        ReportService.java
        impl/
          ReportServiceImpl.java
      admin/
        AdminService.java
        impl/
          AdminServiceImpl.java
      notification/
        NotificationService.java
        impl/
          NotificationServiceImpl.java
      dashboard/
        DashboardService.java
        impl/
          DashboardServiceImpl.java
    repository/
      UserRepository.java
      StudentProfileRepository.java
      OrganizationProfileRepository.java
      OpportunityRepository.java
      OpportunityCategoryRepository.java
      TagRepository.java
      SkillRepository.java
      BookmarkRepository.java
      ApplicationRepository.java
      ReportRepository.java
      AdminReviewRepository.java
      NotificationRepository.java
      ResumeRepository.java
    entity/
      BaseEntity.java
      User.java
      StudentProfile.java
      OrganizationProfile.java
      Opportunity.java
      OpportunityCategory.java
      Tag.java
      Skill.java
      Bookmark.java
      Application.java
      Report.java
      AdminReview.java
      Notification.java
      Resume.java
    dto/
      request/
        auth/
        student/
        organization/
        opportunity/
        bookmark/
        application/
        report/
        admin/
      response/
        auth/
        student/
        organization/
        opportunity/
        bookmark/
        application/
        report/
        admin/
        dashboard/
    infrastructure/
      mail/
        MailService.java
        SmtpMailService.java
      storage/
        StorageService.java
        CloudinaryStorageService.java
      template/
        EmailTemplateService.java
    security/
      JwtService.java
      JwtAuthenticationFilter.java
      CustomUserDetails.java
      CustomUserDetailsService.java
    scheduler/
      DeadlineReminderScheduler.java
      WeeklyDigestScheduler.java
    common/
      dto/
        ApiResponse.java
        PageResponse.java
        ErrorResponse.java
      exception/
        GlobalExceptionHandler.java
        BusinessException.java
        ResourceNotFoundException.java
      enums/
        UserRole.java
        UserStatus.java
        OpportunityStatus.java
        ApplicationStatus.java
        ReportStatus.java
      util/
        SecurityUtils.java
        DateUtils.java
  src/main/resources/
    application.yml
    application-dev.yml
    application-prod.yml
  src/test/java/com/opportunityboard/
```

---

## Steps

### 1. Khởi tạo cấu trúc dự án

Mục tiêu:

- Tạo frontend React + TypeScript + Vite.
- Tạo backend Spring Boot 3 + Maven.
- Tạo cấu trúc thư mục ban đầu.
- Cấu hình môi trường local.

Thay đổi dự kiến:

| Khu vực | File/thư mục |
|---|---|
| Root | `README.md`, `.gitignore` |
| Frontend | `frontend/.env.example` |
| Backend | `backend/.env.example` |
| Frontend | `frontend/package.json`, `frontend/vite.config.ts`, `frontend/tsconfig.json`, `frontend/tailwind.config.js` |
| Frontend | `frontend/src/main.tsx`, `frontend/src/App.tsx`, `frontend/src/routes/AppRoutes.tsx` |
| Backend | `backend/pom.xml`, `backend/src/main/resources/application.yml` |
| Backend | `backend/src/main/java/com/opportunityboard/OpportunityBoardApplication.java` |

Completion criteria:

- Chạy được frontend bằng `npm run dev`.
- Chạy được backend bằng Maven/Spring Boot.
- Backend có endpoint health check trả về OK.
- `frontend/.env.example` và `backend/.env.example` liệt kê biến môi trường cần thiết cho từng app.

---

### 2. Thiết kế database và entity nền tảng

Mục tiêu:

- Tạo schema dữ liệu cốt lõi.
- Mapping entity bằng JPA/Hibernate.
- Chuẩn bị dữ liệu nền cho role, category, tag.

Thay đổi dự kiến:

| Khu vực | File/thư mục |
|---|---|
| Backend entity | `backend/src/main/java/com/opportunityboard/entity/BaseEntity.java` |
| Backend entity | `backend/src/main/java/com/opportunityboard/entity/User.java` |
| Backend entity | `backend/src/main/java/com/opportunityboard/entity/StudentProfile.java` |
| Backend entity | `backend/src/main/java/com/opportunityboard/entity/OrganizationProfile.java` |
| Backend entity | `backend/src/main/java/com/opportunityboard/entity/Opportunity.java` |
| Backend entity | `backend/src/main/java/com/opportunityboard/entity/OpportunityCategory.java` |
| Backend entity | `backend/src/main/java/com/opportunityboard/entity/Tag.java` |
| Backend entity | `backend/src/main/java/com/opportunityboard/entity/Skill.java` |
| Backend entity | `backend/src/main/java/com/opportunityboard/entity/Application.java` |
| Backend entity | `backend/src/main/java/com/opportunityboard/entity/Bookmark.java` |
| Backend entity | `backend/src/main/java/com/opportunityboard/entity/Notification.java` |
| Backend entity | `backend/src/main/java/com/opportunityboard/entity/Report.java` |
| Backend entity | `backend/src/main/java/com/opportunityboard/entity/AdminReview.java` |
| Backend repository | `backend/src/main/java/com/opportunityboard/repository/*Repository.java` |
| Backend enum | `backend/src/main/java/com/opportunityboard/common/enums/*Status.java`, `UserRole.java` |

Entity chính:

| Entity | Quan hệ quan trọng |
|---|---|
| `User` | One-to-one với `StudentProfile` hoặc `OrganizationProfile` |
| `OrganizationProfile` | One-to-many với `Opportunity` |
| `StudentProfile` | One-to-many với `Bookmark`, `Application`, `Resume` |
| `Opportunity` | One-to-many với `Application`, `Bookmark`, `Report`, `AdminReview` |
| `Opportunity` | Many-to-many với `Tag` |
| `StudentProfile` | Many-to-many với `Skill` |

Completion criteria:

- Ứng dụng backend khởi động và tự tạo/migrate schema được.
- Các quan hệ JPA không gây vòng lặp serialization.
- Có seed dữ liệu role và category cơ bản.

---

### 3. Xây dựng authentication và authorization

Mục tiêu:

- Đăng ký, đăng nhập bằng email/password.
- Hash password.
- Cấp JWT.
- Bảo vệ API theo role.

Thay đổi dự kiến:

| Khu vực | File/thư mục |
|---|---|
| Backend config | `backend/src/main/java/com/opportunityboard/config/SecurityConfig.java` |
| Backend security | `backend/src/main/java/com/opportunityboard/security/JwtService.java` |
| Backend security | `backend/src/main/java/com/opportunityboard/security/JwtAuthenticationFilter.java` |
| Backend security | `backend/src/main/java/com/opportunityboard/security/CustomUserDetailsService.java` |
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/AuthController.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/auth/AuthService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/auth/impl/AuthServiceImpl.java` |
| Backend DTO request | `backend/src/main/java/com/opportunityboard/dto/request/auth/RegisterRequest.java` |
| Backend DTO request | `backend/src/main/java/com/opportunityboard/dto/request/auth/LoginRequest.java` |
| Backend DTO response | `backend/src/main/java/com/opportunityboard/dto/response/auth/AuthResponse.java` |
| Frontend auth | `frontend/src/features/auth/components/*`, `frontend/src/features/auth/hooks/*`, `frontend/src/features/auth/api/authApi.ts` |
| Frontend pages | `frontend/src/pages/auth/LoginPage.tsx`, `frontend/src/pages/auth/RegisterPage.tsx` |
| Frontend routes | `frontend/src/routes/ProtectedRoute.tsx` |
| Frontend routes | `frontend/src/routes/RoleBasedRoute.tsx` |
| Frontend context | `frontend/src/context/AuthContext.tsx` |

API:

| Method | Endpoint | Quyền |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| GET | `/api/auth/me` | Authenticated |
| POST | `/api/auth/refresh-token` | Authenticated |

Completion criteria:

- Student, Organization, Admin đăng nhập được.
- JWT được lưu và gửi kèm request từ frontend.
- API student không truy cập được bằng organization token.
- API admin không truy cập được bằng student/organization token.

---

### 4. Xây dựng hồ sơ sinh viên và tổ chức

Mục tiêu:

- Sinh viên cập nhật hồ sơ cá nhân.
- Sinh viên upload CV.
- Tổ chức cập nhật hồ sơ, logo, website.

Thay đổi dự kiến:

| Khu vực | File/thư mục |
|---|---|
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/StudentController.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/student/StudentService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/student/impl/StudentServiceImpl.java` |
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/OrganizationController.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/organization/OrganizationService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/organization/impl/OrganizationServiceImpl.java` |
| Backend infrastructure | `backend/src/main/java/com/opportunityboard/infrastructure/storage/StorageService.java` |
| Backend infrastructure | `backend/src/main/java/com/opportunityboard/infrastructure/storage/CloudinaryStorageService.java` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/request/student/*`, `backend/src/main/java/com/opportunityboard/dto/response/student/*` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/request/organization/*`, `backend/src/main/java/com/opportunityboard/dto/response/organization/*` |
| Frontend student feature | `frontend/src/features/students/components/*`, `frontend/src/features/students/api/studentApi.ts` |
| Frontend organization feature | `frontend/src/features/organizations/components/*`, `frontend/src/features/organizations/api/organizationApi.ts` |
| Frontend pages | `frontend/src/pages/student/ProfilePage.tsx`, `frontend/src/pages/organization/OrganizationProfilePage.tsx` |
| Frontend upload | `frontend/src/components/forms/FileUpload.tsx` |

API:

| Method | Endpoint | Quyền |
|---|---|---|
| GET | `/api/students/me` | Student |
| PATCH | `/api/students/me` | Student |
| POST | `/api/students/me/resume` | Student |
| GET | `/api/organizations/me` | Organization |
| PATCH | `/api/organizations/me` | Organization |

Completion criteria:

- Sinh viên cập nhật được trường, ngành, năm học, kỹ năng, sở thích.
- Sinh viên upload được CV lên Cloudinary.
- Tổ chức cập nhật được tên, logo, mô tả, website.
- Frontend hiển thị lỗi validation rõ ràng.

---

### 5. Xây dựng opportunity workflow

Mục tiêu:

- Tổ chức tạo, sửa, đóng bài đăng.
- Bài đăng mới ở trạng thái `PENDING`.
- Admin duyệt hoặc từ chối bài đăng.
- Sinh viên chỉ thấy cơ hội `APPROVED`.

Thay đổi dự kiến:

| Khu vực | File/thư mục |
|---|---|
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/OpportunityController.java` |
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/AdminController.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/opportunity/OpportunityService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/opportunity/impl/OpportunityServiceImpl.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/admin/AdminService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/admin/impl/AdminServiceImpl.java` |
| Backend repository | `backend/src/main/java/com/opportunityboard/repository/OpportunityRepository.java` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/request/opportunity/*`, `backend/src/main/java/com/opportunityboard/dto/response/opportunity/*` |
| Frontend opportunity feature | `frontend/src/features/opportunities/components/OpportunityForm.tsx` |
| Frontend opportunity feature | `frontend/src/features/opportunities/components/OpportunityDetail.tsx` |
| Frontend opportunity API | `frontend/src/features/opportunities/api/opportunityApi.ts` |
| Frontend pages | `frontend/src/pages/public/OpportunityDetailPage.tsx` |
| Frontend pages | `frontend/src/pages/organization/OrganizationOpportunitiesPage.tsx` |
| Frontend pages | `frontend/src/pages/admin/PendingOpportunitiesPage.tsx` |

API:

| Method | Endpoint | Quyền |
|---|---|---|
| POST | `/api/opportunities` | Organization |
| PATCH | `/api/opportunities/{id}` | Organization owner |
| DELETE | `/api/opportunities/{id}` | Organization owner hoặc Admin |
| GET | `/api/opportunities/{id}` | Public/Auth |
| GET | `/api/admin/opportunities/pending` | Admin |
| POST | `/api/admin/opportunities/{id}/approve` | Admin |
| POST | `/api/admin/opportunities/{id}/reject` | Admin |

Completion criteria:

- Organization tạo bài xong thì bài chờ duyệt.
- Admin approve thì bài xuất hiện trong danh sách public.
- Admin reject thì bài không xuất hiện public và có lý do từ chối.
- Organization không sửa được bài của tổ chức khác.

---

### 6. Xây dựng search, filter và discovery

Mục tiêu:

- Danh sách cơ hội có tìm kiếm keyword.
- Filter theo category, location, deadline, field, skill.
- Sort theo mới nhất, deadline gần nhất, phổ biến nhất.
- Gợi ý cơ hội rule-based, không dùng AI.

Thay đổi dự kiến:

| Khu vực | File/thư mục |
|---|---|
| Backend search | `backend/src/main/java/com/opportunityboard/service/opportunity/OpportunitySearchService.java` |
| Backend search impl | `backend/src/main/java/com/opportunityboard/service/opportunity/impl/OpportunitySearchServiceImpl.java` |
| Backend DTO request | `backend/src/main/java/com/opportunityboard/dto/request/opportunity/OpportunitySearchRequest.java` |
| Backend repository | Query full-text search trong `OpportunityRepository` |
| Frontend explore page | `frontend/src/pages/public/ExplorePage.tsx` |
| Frontend filter | `frontend/src/features/opportunities/components/OpportunityFilters.tsx` |
| Frontend card | `frontend/src/features/opportunities/components/OpportunityCard.tsx` |
| Frontend API | `frontend/src/features/opportunities/api/opportunityApi.ts` |

Completion criteria:

- Search theo tiêu đề, tổ chức, mô tả hoạt động.
- Filter có thể kết hợp nhiều điều kiện.
- Pagination hoạt động.
- Chỉ trả về opportunity `APPROVED` và chưa đóng/hết hạn, trừ màn hình quản trị.

---

### 7. Xây dựng bookmark

Mục tiêu:

- Sinh viên lưu/bỏ lưu cơ hội.
- Sinh viên xem danh sách đã lưu.
- Saved count cập nhật đúng.

Thay đổi dự kiến:

| Khu vực | File/thư mục |
|---|---|
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/BookmarkController.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/bookmark/BookmarkService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/bookmark/impl/BookmarkServiceImpl.java` |
| Backend repository | `backend/src/main/java/com/opportunityboard/repository/BookmarkRepository.java` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/response/bookmark/*` |
| Frontend saved page | `frontend/src/pages/student/SavedOpportunitiesPage.tsx` |
| Frontend bookmark feature | `frontend/src/features/bookmarks/components/*`, `frontend/src/features/bookmarks/api/bookmarkApi.ts` |
| Frontend components | Nút save/unsave trong `features/opportunities/components/OpportunityCard.tsx` và `OpportunityDetail.tsx` |

API:

| Method | Endpoint | Quyền |
|---|---|---|
| POST | `/api/opportunities/{id}/bookmark` | Student |
| DELETE | `/api/opportunities/{id}/bookmark` | Student |
| GET | `/api/bookmarks/me` | Student |

Completion criteria:

- Sinh viên không bookmark trùng một opportunity.
- Bỏ lưu cập nhật UI ngay.
- Danh sách saved sort được theo deadline gần nhất.

---

### 8. Xây dựng application tracker

Mục tiêu:

- Sinh viên ứng tuyển.
- Tổ chức xem danh sách ứng viên theo bài đăng.
- Tổ chức cập nhật trạng thái ứng tuyển.
- Sinh viên theo dõi trạng thái.

Thay đổi dự kiến:

| Khu vực | File/thư mục |
|---|---|
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/ApplicationController.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/application/ApplicationService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/application/impl/ApplicationServiceImpl.java` |
| Backend repository | `backend/src/main/java/com/opportunityboard/repository/ApplicationRepository.java` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/request/application/*`, `backend/src/main/java/com/opportunityboard/dto/response/application/*` |
| Frontend applications page | `frontend/src/pages/student/ApplicationsPage.tsx` |
| Frontend applications feature | `frontend/src/features/applications/components/ApplicationTracker.tsx` |
| Frontend applications API | `frontend/src/features/applications/api/applicationApi.ts` |
| Frontend applicants page | `frontend/src/pages/organization/ApplicantsPage.tsx` |

API:

| Method | Endpoint | Quyền |
|---|---|---|
| POST | `/api/opportunities/{id}/apply` | Student |
| GET | `/api/applications/me` | Student |
| GET | `/api/applications/{id}` | Student owner hoặc Organization owner |
| PATCH | `/api/applications/{id}/status` | Organization owner |
| GET | `/api/organizations/me/applications` | Organization |

Completion criteria:

- Sinh viên không ứng tuyển trùng một opportunity.
- Organization chỉ xem và cập nhật application thuộc opportunity của mình.
- Student dashboard hiển thị trạng thái mới nhất.
- Khi trạng thái đổi, notification/email được tạo.

---

### 9. Xây dựng report và admin management

Mục tiêu:

- Sinh viên báo cáo bài đăng sai, lừa đảo hoặc đáng ngờ.
- Admin xem và xử lý report.
- Admin khóa/mở khóa user.
- Admin quản lý category, tag, field.

Thay đổi dự kiến:

| Khu vực | File/thư mục |
|---|---|
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/ReportController.java` |
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/AdminController.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/report/ReportService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/report/impl/ReportServiceImpl.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/admin/AdminService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/admin/impl/AdminServiceImpl.java` |
| Backend repository | `backend/src/main/java/com/opportunityboard/repository/ReportRepository.java` |
| Backend repository | `backend/src/main/java/com/opportunityboard/repository/AdminReviewRepository.java` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/request/report/*`, `backend/src/main/java/com/opportunityboard/dto/response/report/*` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/request/admin/*`, `backend/src/main/java/com/opportunityboard/dto/response/admin/*` |
| Frontend admin pages | `frontend/src/pages/admin/ReportsPage.tsx`, `frontend/src/pages/admin/UsersPage.tsx`, `frontend/src/pages/admin/CategoriesPage.tsx` |
| Frontend admin feature | `frontend/src/features/admin/components/*`, `frontend/src/features/admin/api/adminApi.ts` |

API:

| Method | Endpoint | Quyền |
|---|---|---|
| POST | `/api/opportunities/{id}/reports` | Student |
| GET | `/api/admin/reports` | Admin |
| PATCH | `/api/admin/reports/{id}/status` | Admin |
| PATCH | `/api/admin/users/{id}/status` | Admin |
| GET/POST/PATCH/DELETE | `/api/admin/categories` | Admin |

Completion criteria:

- Student report được opportunity.
- Admin thấy report và cập nhật trạng thái xử lý.
- User bị khóa không đăng nhập hoặc thao tác được.
- Category/tag dùng được trong form tạo opportunity và filter.

---

### 10. Xây dựng notification và scheduled jobs

Mục tiêu:

- Gửi email xác nhận hoặc thông báo cần thiết.
- Gửi email khi trạng thái ứng tuyển thay đổi.
- Nhắc deadline cho cơ hội đã lưu hoặc đã ứng tuyển.
- Weekly digest cơ bản.

Thay đổi dự kiến:

| Khu vực | File/thư mục |
|---|---|
| Backend entity | `backend/src/main/java/com/opportunityboard/entity/Notification.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/notification/NotificationService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/notification/impl/NotificationServiceImpl.java` |
| Backend infrastructure | `backend/src/main/java/com/opportunityboard/infrastructure/mail/MailService.java` |
| Backend infrastructure | `backend/src/main/java/com/opportunityboard/infrastructure/mail/SmtpMailService.java` |
| Backend infrastructure | `backend/src/main/java/com/opportunityboard/infrastructure/template/EmailTemplateService.java` |
| Backend scheduler | `backend/src/main/java/com/opportunityboard/scheduler/DeadlineReminderScheduler.java` |
| Backend scheduler | `backend/src/main/java/com/opportunityboard/scheduler/WeeklyDigestScheduler.java` |
| Frontend notification feature | `frontend/src/features/notifications/components/*`, `frontend/src/features/notifications/api/notificationApi.ts` |

Completion criteria:

- SMTP gửi được email trong môi trường dev.
- Scheduler tìm đúng cơ hội sắp hết hạn.
- Không gửi trùng reminder cho cùng user/opportunity trong cùng mốc nhắc.
- Notification được lưu để hiển thị trong dashboard.

---

### 11. Xây dựng dashboard cho 3 vai trò

Mục tiêu:

- Student dashboard hiển thị cơ hội đề xuất rule-based, saved, applications, deadline gần nhất.
- Organization dashboard hiển thị bài đăng, ứng viên, thống kê cơ bản.
- Admin dashboard hiển thị bài chờ duyệt, report, user, opportunity, application metrics.

Thay đổi dự kiến:

| Khu vực | File/thư mục |
|---|---|
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/DashboardController.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/dashboard/DashboardService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/dashboard/impl/DashboardServiceImpl.java` |
| Backend DTO response | `backend/src/main/java/com/opportunityboard/dto/response/dashboard/*` |
| Frontend student page | `frontend/src/pages/student/StudentDashboardPage.tsx` |
| Frontend organization page | `frontend/src/pages/organization/OrganizationDashboardPage.tsx` |
| Frontend admin page | `frontend/src/pages/admin/AdminDashboardPage.tsx` |
| Frontend layout | `frontend/src/layouts/DashboardLayout.tsx`, `frontend/src/layouts/AdminLayout.tsx` |
| Frontend navigation | `frontend/src/components/navigation/Sidebar.tsx` |

Completion criteria:

- Mỗi role đăng nhập xong vào đúng dashboard.
- Dashboard chỉ gọi API phù hợp với role.
- Số liệu hiển thị nhất quán với database.
- UI responsive trên desktop và mobile.

---

### 12. Hoàn thiện UI, validation và API documentation

Mục tiêu:

- Chuẩn hóa UI theo phong cách dashboard hiện đại.
- Thêm loading, empty state, error state.
- Chuẩn hóa validation frontend/backend.
- Hoàn thiện Swagger/OpenAPI.

Thay đổi dự kiến:

| Khu vực | File/thư mục |
|---|---|
| Frontend UI | `frontend/src/components/ui/*` |
| Frontend layout | `frontend/src/layouts/*` |
| Frontend config | `frontend/src/config/apiClient.ts`, `frontend/src/config/constants.ts` |
| Frontend utils | `frontend/src/utils/*`, `frontend/src/hooks/*` |
| Backend config | `backend/src/main/java/com/opportunityboard/config/OpenApiConfig.java` |
| Backend exception | `backend/src/main/java/com/opportunityboard/common/exception/GlobalExceptionHandler.java` |
| Backend common DTO | `backend/src/main/java/com/opportunityboard/common/dto/ApiResponse.java`, `PageResponse.java`, `ErrorResponse.java` |
| Backend validation | DTO request trong `backend/src/main/java/com/opportunityboard/dto/request/**` |

Completion criteria:

- Form hiển thị lỗi validation dễ hiểu.
- API trả error response thống nhất.
- Swagger hiển thị đầy đủ API chính.
- Không có màn hình chính bị vỡ layout trên mobile.

---

### 13. Testing và kiểm thử MVP

Mục tiêu:

- Test các service quan trọng.
- Test phân quyền.
- Test flow chính thủ công từ frontend.

Thay đổi dự kiến:

| Khu vực | File/thư mục |
|---|---|
| Backend tests | `backend/src/test/java/com/opportunityboard/service/auth/AuthServiceTest.java` |
| Backend tests | `backend/src/test/java/com/opportunityboard/service/opportunity/OpportunityServiceTest.java` |
| Backend tests | `backend/src/test/java/com/opportunityboard/service/application/ApplicationServiceTest.java` |
| Backend tests | `backend/src/test/java/com/opportunityboard/service/bookmark/BookmarkServiceTest.java` |
| Backend tests | `backend/src/test/java/com/opportunityboard/service/admin/AdminServiceTest.java` |
| Backend integration tests | `backend/src/test/java/com/opportunityboard/controller/*ControllerIntegrationTest.java` |
| Manual QA | `docs/qa-checklist.md` |

Completion criteria:

- Backend unit tests pass.
- Integration test cho auth và role-based access pass.
- Manual QA pass cho 3 flow chính:
  - Student: đăng ký -> tìm cơ hội -> lưu -> ứng tuyển -> xem trạng thái.
  - Organization: đăng ký -> tạo cơ hội -> xem ứng viên -> cập nhật trạng thái.
  - Admin: duyệt bài -> xử lý report -> khóa/mở user.

---

### 14. Deployment preparation

Mục tiêu:

- Chuẩn bị deploy frontend, backend, database.
- Cấu hình env production.
- Kiểm tra CORS, JWT secret, SMTP, Cloudinary.

Thay đổi dự kiến:

| Khu vực | File/thư mục |
|---|---|
| Frontend deploy | `frontend/vercel.json` nếu cần |
| Backend deploy | `backend/Dockerfile` hoặc cấu hình Render/Railway |
| Root docs | `README.md` |
| Env docs | `frontend/.env.example`, `backend/.env.example` |

Completion criteria:

- Frontend deploy được lên Vercel.
- Backend deploy được lên Render hoặc Railway.
- Backend kết nối được Neon PostgreSQL.
- Frontend gọi được backend production.
- Swagger production truy cập được nếu bật public docs.

---

## Milestones

| Milestone | Nội dung | Kết quả cần có |
|---|---|---|
| M1 | Project setup | Frontend/backend/database chạy local |
| M2 | Auth & roles | Đăng ký, đăng nhập, phân quyền hoạt động |
| M3 | Profiles | Student/organization profile và upload file |
| M4 | Opportunity workflow | Tạo, duyệt, public, xem chi tiết cơ hội |
| M5 | Discovery | Search, filter, sort, bookmark |
| M6 | Applications | Ứng tuyển và cập nhật trạng thái |
| M7 | Admin & notifications | Report, user management, email reminder |
| M8 | Dashboard & polish | Dashboard 3 vai trò, validation, responsive UI |
| M9 | Testing & deploy | Test pass, deploy được staging/production |

## Phase Files

Các phase chi tiết để build dự án nằm trong `docs/phases/`.

| Phase | File |
|---|---|
| 01 | `docs/phases/01-project-setup.md` |
| 02 | `docs/phases/02-database-entities.md` |
| 03 | `docs/phases/03-auth-authorization.md` |
| 04 | `docs/phases/04-profiles-storage.md` |
| 05 | `docs/phases/05-opportunity-workflow.md` |
| 06 | `docs/phases/06-search-discovery.md` |
| 07 | `docs/phases/07-bookmarks.md` |
| 08 | `docs/phases/08-applications.md` |
| 09 | `docs/phases/09-admin-report-management.md` |
| 10 | `docs/phases/10-notifications-schedulers.md` |
| 11 | `docs/phases/11-dashboards.md` |
| 12 | `docs/phases/12-ui-validation-docs.md` |
| 13 | `docs/phases/13-testing-qa.md` |
| 14 | `docs/phases/14-deployment.md` |

---

## Risks và cách kiểm tra

| Rủi ro | Tác động | Cách giảm rủi ro | Cách kiểm tra |
|---|---|---|---|
| Scope MVP bị mở rộng | Trễ tiến độ | Giữ ngoài phạm vi các tính năng AI/social/ATS | Review `docs/PLAN.md` trước mỗi milestone |
| Phân quyền sai | Lộ dữ liệu hoặc thao tác sai role | Kiểm tra role ở backend, không chỉ frontend | Integration test cho API Student/Organization/Admin |
| Organization sửa bài của tổ chức khác | Lỗi bảo mật nghiêm trọng | Check ownership trong service layer | Unit test ownership cho opportunity/application |
| Search/filter chậm | Trải nghiệm kém | Dùng pagination và index phù hợp | Test với seed data lớn |
| Email nhắc deadline gửi trùng | Làm phiền người dùng | Lưu lịch sử reminder đã gửi | Test scheduler với dữ liệu deadline mẫu |
| Upload file không an toàn | Rủi ro file sai định dạng/quá lớn | Validate MIME type, extension, size | Test upload file hợp lệ và không hợp lệ |
| Admin duyệt nhầm nội dung xấu | Giảm độ tin cậy | Bắt buộc lý do khi reject, lưu audit log | Manual QA quy trình duyệt/report |

---

## Completion Criteria

MVP hoàn thành khi các điều kiện sau đều đạt:

- Student đăng ký, đăng nhập, cập nhật hồ sơ và upload CV được.
- Organization đăng ký, cập nhật hồ sơ và tạo bài đăng được.
- Opportunity mới phải qua trạng thái `PENDING` trước khi public.
- Admin duyệt/từ chối opportunity được và lịch sử duyệt được lưu.
- Public/student user tìm kiếm, lọc, sort và xem chi tiết opportunity được.
- Student bookmark, bỏ bookmark và xem danh sách saved được.
- Student ứng tuyển một opportunity và không thể ứng tuyển trùng.
- Organization xem đúng ứng viên của mình và cập nhật trạng thái được.
- Student xem được application tracker với trạng thái mới nhất.
- Student report được opportunity.
- Admin xử lý report và khóa/mở user được.
- Email notification hoạt động cho deadline reminder và application status update.
- Dashboard cơ bản cho Student, Organization, Admin hoạt động.
- Swagger/OpenAPI mô tả đủ các API chính.
- Backend test pass cho auth, opportunity, bookmark, application, admin.
- Frontend responsive ở desktop và mobile cho các màn hình chính.
- Có tài liệu chạy local và biến môi trường trong `README.md`, `frontend/.env.example` và `backend/.env.example`.

---

## Quyết định triển khai

Hướng triển khai được chọn:

> Xây dựng MVP theo kiến trúc `React + TypeScript + Vite` cho frontend và `Spring Boot 3 + PostgreSQL` cho backend, tập trung hoàn thiện core flow `đăng nhập -> hồ sơ -> khám phá cơ hội -> lưu/ứng tuyển -> theo dõi trạng thái -> nhắc deadline`.

Lý do:

- Bám đúng phạm vi đã chốt trong `docs/BRAINSTORM.md`.
- Có module rõ ràng cho từng vai trò.
- Dễ chia việc frontend/backend.
- Dễ kiểm thử theo API và user journey.
- Không bị phân tán bởi AI hoặc social features trong giai đoạn MVP.
