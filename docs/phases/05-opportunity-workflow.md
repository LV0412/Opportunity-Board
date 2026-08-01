# Phase 05 - Opportunity Workflow

## Overview

Phase này xây dựng vòng đời cơ hội: tổ chức tạo bài, admin kiểm duyệt, sinh viên xem bài đã được duyệt.

## Scope

Trong scope:

- Tổ chức tạo/sửa/đóng opportunity.
- Opportunity mới ở trạng thái `PENDING`.
- Admin approve/reject opportunity.
- Public/student xem opportunity `APPROVED`.
- Trang chi tiết opportunity.

Ngoài scope:

- Chưa làm search nâng cao.
- Chưa làm bookmark/application.

## Files/Changes

| Khu vực | File/thư mục |
|---|---|
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/OpportunityController.java` |
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/AdminController.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/opportunity/OpportunityService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/opportunity/impl/OpportunityServiceImpl.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/admin/AdminService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/admin/impl/AdminServiceImpl.java` |
| Backend repository | `backend/src/main/java/com/opportunityboard/repository/OpportunityRepository.java` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/request/opportunity/*` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/response/opportunity/*` |
| Frontend feature | `frontend/src/features/opportunities/components/OpportunityForm.tsx` |
| Frontend feature | `frontend/src/features/opportunities/components/OpportunityDetail.tsx` |
| Frontend API | `frontend/src/features/opportunities/api/opportunityApi.ts` |
| Frontend pages | `frontend/src/pages/public/OpportunityDetailPage.tsx` |
| Frontend pages | `frontend/src/pages/organization/OrganizationOpportunitiesPage.tsx` |
| Frontend pages | `frontend/src/pages/admin/PendingOpportunitiesPage.tsx` |

## APIs

| Method | Endpoint | Quyền |
|---|---|---|
| POST | `/api/opportunities` | Organization |
| PATCH | `/api/opportunities/{id}` | Organization owner |
| DELETE | `/api/opportunities/{id}` | Organization owner hoặc Admin |
| GET | `/api/opportunities/{id}` | Public/Auth |
| GET | `/api/admin/opportunities/pending` | Admin |
| POST | `/api/admin/opportunities/{id}/approve` | Admin |
| POST | `/api/admin/opportunities/{id}/reject` | Admin |

## Completion Criteria

- Organization tạo bài xong thì bài ở `PENDING`.
- Admin approve thì bài xuất hiện public.
- Admin reject thì bài không xuất hiện public và có lý do từ chối.
- Organization không sửa/xóa được bài của tổ chức khác.
- Sinh viên chỉ thấy bài `APPROVED` và chưa đóng.

## Risks & Checks

| Rủi ro | Cách kiểm tra |
|---|---|
| Bài chưa duyệt bị public | Test list/detail với bài `PENDING` |
| Sai ownership | Unit test organization owner |
| Admin action thiếu audit | Kiểm tra `AdminReview` sau approve/reject |
