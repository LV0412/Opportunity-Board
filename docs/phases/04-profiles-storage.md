# Phase 04 - Hồ sơ người dùng và Storage

## Overview

Phase này xây dựng hồ sơ sinh viên, hồ sơ tổ chức và upload file qua Cloudinary.

## Scope

Trong scope:

- Student profile.
- Organization profile.
- Upload CV cho sinh viên.
- Upload logo cho tổ chức.
- Validation file upload.

Ngoài scope:

- Chưa parse CV.
- Chưa chấm điểm hồ sơ.
- Chưa public portfolio sinh viên.

## Files/Changes

| Khu vực | File/thư mục |
|---|---|
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/StudentController.java` |
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/OrganizationController.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/student/StudentService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/student/impl/StudentServiceImpl.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/organization/OrganizationService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/organization/impl/OrganizationServiceImpl.java` |
| Backend infrastructure | `backend/src/main/java/com/opportunityboard/infrastructure/storage/StorageService.java` |
| Backend infrastructure | `backend/src/main/java/com/opportunityboard/infrastructure/storage/CloudinaryStorageService.java` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/request/student/*` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/response/student/*` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/request/organization/*` |
| Backend DTO | `backend/src/main/java/com/opportunityboard/dto/response/organization/*` |
| Frontend student | `frontend/src/pages/student/ProfilePage.tsx` |
| Frontend organization | `frontend/src/pages/organization/OrganizationProfilePage.tsx` |
| Frontend upload | `frontend/src/components/forms/FileUpload.tsx` |
| Frontend APIs | `frontend/src/features/students/api/studentApi.ts` |
| Frontend APIs | `frontend/src/features/organizations/api/organizationApi.ts` |

## APIs

| Method | Endpoint | Quyền |
|---|---|---|
| GET | `/api/students/me` | Student |
| PATCH | `/api/students/me` | Student |
| POST | `/api/students/me/resume` | Student |
| GET | `/api/organizations/me` | Organization |
| PATCH | `/api/organizations/me` | Organization |

## Completion Criteria

- Sinh viên cập nhật được trường, ngành, năm học, kỹ năng, sở thích.
- Sinh viên upload được CV lên Cloudinary.
- Tổ chức cập nhật được tên, logo, mô tả, website.
- File upload validate type và size.
- Frontend hiển thị lỗi validation rõ ràng.

## Risks & Checks

| Rủi ro | Cách kiểm tra |
|---|---|
| Upload file sai định dạng | Test PDF, image, file quá lớn |
| User cập nhật hồ sơ role khác | Test endpoint với token sai role |
| Cloudinary lỗi làm vỡ flow | Mock/fallback error response rõ ràng |
