# Phase 12 - UI Polish, Validation và API Documentation

## Overview

Phase này chuẩn hóa trải nghiệm người dùng, validation frontend/backend, error response và Swagger/OpenAPI.

## Scope

Trong scope:

- Shared UI components.
- Loading, empty state, error state.
- Form validation.
- Global exception handling.
- API response format.
- Swagger/OpenAPI.

Ngoài scope:

- Chưa làm redesign lớn.
- Chưa tối ưu animation phức tạp.

## Files/Changes

| Khu vực | File/thư mục |
|---|---|
| Frontend UI | `frontend/src/components/ui/*` |
| Frontend common | `frontend/src/components/common/*` |
| Frontend forms | `frontend/src/components/forms/*` |
| Frontend layout | `frontend/src/layouts/*` |
| Frontend config | `frontend/src/config/apiClient.ts` |
| Frontend config | `frontend/src/config/constants.ts` |
| Frontend utils | `frontend/src/utils/*` |
| Frontend hooks | `frontend/src/hooks/*` |
| Backend config | `backend/src/main/java/com/opportunityboard/config/OpenApiConfig.java` |
| Backend exception | `backend/src/main/java/com/opportunityboard/common/exception/GlobalExceptionHandler.java` |
| Backend common DTO | `backend/src/main/java/com/opportunityboard/common/dto/ApiResponse.java` |
| Backend common DTO | `backend/src/main/java/com/opportunityboard/common/dto/PageResponse.java` |
| Backend common DTO | `backend/src/main/java/com/opportunityboard/common/dto/ErrorResponse.java` |
| Backend validation | `backend/src/main/java/com/opportunityboard/dto/request/**` |

## Completion Criteria

- Form hiển thị lỗi validation dễ hiểu.
- API trả error response thống nhất.
- Swagger hiển thị đầy đủ API chính.
- UI có loading state.
- UI có empty state cho danh sách rỗng.
- UI có error state cho request lỗi.
- Không có màn hình chính bị vỡ layout trên mobile.

## Risks & Checks

| Rủi ro | Cách kiểm tra |
|---|---|
| Error format không thống nhất | Test các lỗi validation/auth/not found |
| UI text tràn trên mobile | Manual QA responsive |
| Swagger thiếu endpoint | So với API list trong plan |
