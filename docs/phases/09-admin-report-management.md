# Phase 09 - Admin, Reports và Taxonomy Management

## Overview

Phase này hoàn thiện công cụ quản trị: xử lý report, quản lý user, category, tag và field.

## Scope

Trong scope:

- Student report opportunity.
- Admin xem và xử lý report.
- Admin khóa/mở user.
- Admin quản lý category/tag/field.

Ngoài scope:

- Chưa làm phân quyền admin nhiều cấp.
- Chưa làm moderation tự động.

## Files/Changes

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
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/request/report/*` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/response/report/*` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/request/admin/*` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/response/admin/*` |
| Frontend pages | `frontend/src/pages/admin/ReportsPage.tsx` |
| Frontend pages | `frontend/src/pages/admin/UsersPage.tsx` |
| Frontend pages | `frontend/src/pages/admin/CategoriesPage.tsx` |
| Frontend feature | `frontend/src/features/admin/components/*` |
| Frontend API | `frontend/src/features/admin/api/adminApi.ts` |

## APIs

| Method | Endpoint | Quyền |
|---|---|---|
| POST | `/api/opportunities/{id}/reports` | Student |
| GET | `/api/admin/reports` | Admin |
| PATCH | `/api/admin/reports/{id}/status` | Admin |
| PATCH | `/api/admin/users/{id}/status` | Admin |
| GET/POST/PATCH/DELETE | `/api/admin/categories` | Admin |

## Completion Criteria

- Student report được opportunity.
- Admin xem được danh sách report.
- Admin cập nhật được trạng thái report.
- User bị khóa không đăng nhập hoặc thao tác được.
- Category/tag dùng được trong form tạo opportunity và filter.
- Admin action quan trọng có audit log.

## Risks & Checks

| Rủi ro | Cách kiểm tra |
|---|---|
| User bị khóa vẫn dùng token cũ | Check status trong security/service |
| Admin taxonomy làm hỏng filter | Test create/update/delete category/tag |
| Report không gắn đúng opportunity | Test report detail |
