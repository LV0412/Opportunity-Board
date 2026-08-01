# Phase 02 - Database, Entities và Repositories

## Overview

Phase này thiết kế domain data nền tảng cho Opportunity Board bằng PostgreSQL, JPA entities, repositories và enum dùng chung.

## Scope

Trong scope:

- Tạo entity chính.
- Tạo repository cho từng entity.
- Tạo enum trạng thái và role.
- Tạo `BaseEntity`.
- Seed dữ liệu role/category/tag cơ bản nếu cần.

Ngoài scope:

- Chưa build business logic đầy đủ.
- Chưa expose REST API cho toàn bộ entity.

## Files/Changes

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
| Backend entity | `backend/src/main/java/com/opportunityboard/entity/Resume.java` |
| Backend repository | `backend/src/main/java/com/opportunityboard/repository/*Repository.java` |
| Backend enums | `backend/src/main/java/com/opportunityboard/common/enums/*` |

## Core Entities

| Entity | Quan hệ quan trọng |
|---|---|
| `User` | One-to-one với `StudentProfile` hoặc `OrganizationProfile` |
| `OrganizationProfile` | One-to-many với `Opportunity` |
| `StudentProfile` | One-to-many với `Bookmark`, `Application`, `Resume` |
| `Opportunity` | One-to-many với `Application`, `Bookmark`, `Report`, `AdminReview` |
| `Opportunity` | Many-to-many với `Tag` |
| `StudentProfile` | Many-to-many với `Skill` |

## Required Enums

- `UserRole`: `STUDENT`, `ORGANIZATION`, `ADMIN`
- `UserStatus`: `ACTIVE`, `LOCKED`, `DISABLED`
- `OpportunityStatus`: `DRAFT`, `PENDING`, `APPROVED`, `REJECTED`, `CLOSED`
- `ApplicationStatus`: `APPLIED`, `REVIEWING`, `ACCEPTED`, `REJECTED`
- `ReportStatus`: `PENDING`, `RESOLVED`, `REJECTED`

## Completion Criteria

- Backend khởi động được với PostgreSQL.
- Schema được tạo/migrate thành công.
- Các entity có quan hệ đúng.
- Không có vòng lặp JSON serialization khi trả DTO.
- Có repository cho các entity chính.
- Có seed category/tag/role cơ bản nếu app cần dữ liệu nền.

## Risks & Checks

| Rủi ro | Cách kiểm tra |
|---|---|
| Quan hệ JPA sai gây lỗi startup | Chạy backend với schema sạch |
| Cascade delete làm mất dữ liệu | Review mapping và viết test repository |
| Enum không thống nhất với frontend | Xuất enum qua DTO hoặc constants rõ ràng |
