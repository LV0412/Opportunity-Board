# Phase 11 - Dashboards theo vai trò

## Overview

Phase này xây dựng dashboard riêng cho Student, Organization và Admin để người dùng thấy thông tin quan trọng sau khi đăng nhập.

## Scope

Trong scope:

- Student dashboard.
- Organization dashboard.
- Admin dashboard.
- Dashboard API.
- Layout và sidebar theo role.

Ngoài scope:

- Chưa làm analytics nâng cao.
- Chưa làm biểu đồ phức tạp.

## Files/Changes

| Khu vực | File/thư mục |
|---|---|
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/DashboardController.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/dashboard/DashboardService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/dashboard/impl/DashboardServiceImpl.java` |
| Backend DTO response | `backend/src/main/java/com/opportunityboard/dto/response/dashboard/*` |
| Frontend page | `frontend/src/pages/student/StudentDashboardPage.tsx` |
| Frontend page | `frontend/src/pages/organization/OrganizationDashboardPage.tsx` |
| Frontend page | `frontend/src/pages/admin/AdminDashboardPage.tsx` |
| Frontend layout | `frontend/src/layouts/DashboardLayout.tsx` |
| Frontend layout | `frontend/src/layouts/AdminLayout.tsx` |
| Frontend navigation | `frontend/src/components/navigation/Sidebar.tsx` |

## Dashboard Content

| Role | Nội dung |
|---|---|
| Student | Cơ hội đề xuất rule-based, saved, applications, deadline gần nhất |
| Organization | Bài đăng, ứng viên, thống kê lượt xem/lưu/ứng tuyển |
| Admin | Bài chờ duyệt, report, user count, opportunity count, application count |

## Completion Criteria

- Mỗi role đăng nhập xong vào đúng dashboard.
- Dashboard chỉ gọi API phù hợp với role.
- Số liệu nhất quán với database.
- Sidebar hiển thị menu theo role.
- UI responsive trên desktop và mobile.

## Risks & Checks

| Rủi ro | Cách kiểm tra |
|---|---|
| Role thấy menu sai | Test từng role |
| Dashboard query nặng | Dùng aggregate query/pagination |
| Số liệu lệch | So dashboard với dữ liệu database mẫu |
