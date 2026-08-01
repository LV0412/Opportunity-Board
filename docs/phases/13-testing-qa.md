# Phase 13 - Testing và Manual QA

## Overview

Phase này tập trung kiểm thử service quan trọng, phân quyền API và các flow chính từ frontend.

## Scope

Trong scope:

- Backend unit tests.
- Backend integration tests.
- Manual QA checklist.
- Kiểm thử 3 user journey chính.

Ngoài scope:

- Chưa bắt buộc E2E automated bằng Playwright/Cypress.
- Chưa làm performance test quy mô lớn.

## Files/Changes

| Khu vực | File/thư mục |
|---|---|
| Backend tests | `backend/src/test/java/com/opportunityboard/service/auth/AuthServiceTest.java` |
| Backend tests | `backend/src/test/java/com/opportunityboard/service/opportunity/OpportunityServiceTest.java` |
| Backend tests | `backend/src/test/java/com/opportunityboard/service/application/ApplicationServiceTest.java` |
| Backend tests | `backend/src/test/java/com/opportunityboard/service/bookmark/BookmarkServiceTest.java` |
| Backend tests | `backend/src/test/java/com/opportunityboard/service/admin/AdminServiceTest.java` |
| Backend integration tests | `backend/src/test/java/com/opportunityboard/controller/*ControllerIntegrationTest.java` |
| Manual QA | `docs/qa-checklist.md` |

## Manual QA Flows

### Student

1. Đăng ký tài khoản student.
2. Đăng nhập.
3. Cập nhật hồ sơ.
4. Tìm cơ hội.
5. Lưu cơ hội.
6. Ứng tuyển.
7. Xem trạng thái trong application tracker.

### Organization

1. Đăng ký tài khoản organization.
2. Cập nhật hồ sơ tổ chức.
3. Tạo opportunity.
4. Chờ admin duyệt.
5. Xem applicant.
6. Cập nhật trạng thái application.

### Admin

1. Đăng nhập admin.
2. Duyệt opportunity.
3. Từ chối opportunity.
4. Xử lý report.
5. Khóa/mở user.

## Completion Criteria

- Backend unit tests pass.
- Integration test cho auth và role-based access pass.
- Manual QA pass cho 3 flow chính.
- Không có lỗi phân quyền nghiêm trọng.
- Không có regression ở opportunity workflow.

## Risks & Checks

| Rủi ro | Cách kiểm tra |
|---|---|
| Test thiếu case ownership | Thêm test organization A/B |
| Manual QA không lặp lại được | Viết `docs/qa-checklist.md` theo checklist |
| Dữ liệu test không ổn định | Dùng seed/test fixtures |
