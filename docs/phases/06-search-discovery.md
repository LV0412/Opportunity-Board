# Phase 06 - Search, Filter và Discovery

## Overview

Phase này giúp sinh viên khám phá cơ hội nhanh bằng keyword search, filter, sort, pagination và gợi ý rule-based không dùng AI.

## Scope

Trong scope:

- Keyword search bằng PostgreSQL Full-Text Search.
- Filter theo category, location, deadline, field, skill.
- Sort theo mới nhất, deadline gần nhất, phổ biến nhất.
- Pagination.
- Explore page.
- Opportunity card.

Ngoài scope:

- Không dùng Elasticsearch/Meilisearch trong MVP.
- Không dùng AI recommendation.

## Files/Changes

| Khu vực | File/thư mục |
|---|---|
| Backend service | `backend/src/main/java/com/opportunityboard/service/opportunity/OpportunitySearchService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/opportunity/impl/OpportunitySearchServiceImpl.java` |
| Backend DTO request | `backend/src/main/java/com/opportunityboard/dto/request/opportunity/OpportunitySearchRequest.java` |
| Backend repository | Query full-text search trong `OpportunityRepository` |
| Frontend page | `frontend/src/pages/public/ExplorePage.tsx` |
| Frontend components | `frontend/src/features/opportunities/components/OpportunityFilters.tsx` |
| Frontend components | `frontend/src/features/opportunities/components/OpportunityCard.tsx` |
| Frontend API | `frontend/src/features/opportunities/api/opportunityApi.ts` |

## Completion Criteria

- Search theo tiêu đề, tổ chức, mô tả hoạt động.
- Filter kết hợp nhiều điều kiện.
- Sort hoạt động với `newest`, `deadline`, `popular`.
- Pagination hoạt động.
- API chỉ trả về opportunity `APPROVED`, chưa đóng và còn hạn cho public/student.
- Explore page có loading, empty state và error state.

## Risks & Checks

| Rủi ro | Cách kiểm tra |
|---|---|
| Query chậm khi dữ liệu nhiều | Seed dữ liệu mẫu lớn và đo response time |
| Filter sai khi kết hợp nhiều điều kiện | Test matrix filter |
| Public thấy bài không hợp lệ | Test trạng thái `PENDING`, `REJECTED`, `CLOSED` |
