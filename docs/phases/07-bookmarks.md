# Phase 07 - Bookmark Opportunities

## Overview

Phase này cho phép sinh viên lưu, bỏ lưu và quản lý danh sách cơ hội quan tâm.

## Scope

Trong scope:

- Save opportunity.
- Unsave opportunity.
- Saved opportunities page.
- Save count.
- Sort saved list theo deadline.

Ngoài scope:

- Chưa làm reminder nâng cao.
- Chưa làm collection cá nhân.

## Files/Changes

| Khu vực | File/thư mục |
|---|---|
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/BookmarkController.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/bookmark/BookmarkService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/bookmark/impl/BookmarkServiceImpl.java` |
| Backend repository | `backend/src/main/java/com/opportunityboard/repository/BookmarkRepository.java` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/response/bookmark/*` |
| Frontend page | `frontend/src/pages/student/SavedOpportunitiesPage.tsx` |
| Frontend feature | `frontend/src/features/bookmarks/components/*` |
| Frontend API | `frontend/src/features/bookmarks/api/bookmarkApi.ts` |
| Frontend integration | Save/unsave button trong `OpportunityCard.tsx` và `OpportunityDetail.tsx` |

## APIs

| Method | Endpoint | Quyền |
|---|---|---|
| POST | `/api/opportunities/{id}/bookmark` | Student |
| DELETE | `/api/opportunities/{id}/bookmark` | Student |
| GET | `/api/bookmarks/me` | Student |

## Completion Criteria

- Sinh viên bookmark được opportunity.
- Sinh viên bỏ bookmark được.
- Không bookmark trùng một opportunity.
- Save count cập nhật đúng.
- Saved page hiển thị danh sách đã lưu.
- Saved list sort được theo deadline gần nhất.

## Risks & Checks

| Rủi ro | Cách kiểm tra |
|---|---|
| Bookmark trùng | Unique constraint hoặc service check |
| Save count lệch | Test save/unsave nhiều lần |
| User khác role gọi API | Test organization/admin token |
