# Phase 08 - Applications và Application Tracker

## Overview

Phase này xây dựng luồng ứng tuyển: sinh viên apply, tổ chức xem ứng viên, cập nhật trạng thái, sinh viên theo dõi tiến trình.

## Scope

Trong scope:

- Student apply opportunity.
- Student xem applications của mình.
- Organization xem applicants.
- Organization cập nhật trạng thái.
- Application tracker UI.

Ngoài scope:

- Không làm ATS nhiều vòng phỏng vấn.
- Không làm scoring/ranking ứng viên.

## Files/Changes

| Khu vực | File/thư mục |
|---|---|
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/ApplicationController.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/application/ApplicationService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/application/impl/ApplicationServiceImpl.java` |
| Backend repository | `backend/src/main/java/com/opportunityboard/repository/ApplicationRepository.java` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/request/application/*` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/response/application/*` |
| Frontend page | `frontend/src/pages/student/ApplicationsPage.tsx` |
| Frontend feature | `frontend/src/features/applications/components/ApplicationTracker.tsx` |
| Frontend API | `frontend/src/features/applications/api/applicationApi.ts` |
| Frontend page | `frontend/src/pages/organization/ApplicantsPage.tsx` |

## APIs

| Method | Endpoint | Quyền |
|---|---|---|
| POST | `/api/opportunities/{id}/apply` | Student |
| GET | `/api/applications/me` | Student |
| GET | `/api/applications/{id}` | Student owner hoặc Organization owner |
| PATCH | `/api/applications/{id}/status` | Organization owner |
| GET | `/api/organizations/me/applications` | Organization |

## Completion Criteria

- Sinh viên ứng tuyển được opportunity `APPROVED` và còn hạn.
- Sinh viên không ứng tuyển trùng một opportunity.
- Organization chỉ xem application thuộc opportunity của mình.
- Organization cập nhật được trạng thái.
- Student xem được trạng thái mới nhất trong tracker.
- Khi trạng thái đổi, notification/email được tạo.

## Risks & Checks

| Rủi ro | Cách kiểm tra |
|---|---|
| Organization xem ứng viên của tổ chức khác | Integration test ownership |
| Apply vào bài hết hạn | Test deadline validation |
| Trạng thái không đồng bộ UI | Refresh page sau cập nhật trạng thái |
