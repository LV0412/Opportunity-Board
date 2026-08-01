# Phase 01 - Khởi tạo cấu trúc dự án

## Overview

Phase này tạo nền móng kỹ thuật cho Opportunity Board: frontend React + Vite, backend Spring Boot 3, cấu hình môi trường local và health check backend.

## Scope

Trong scope:

- Tạo thư mục `frontend/`.
- Tạo thư mục `backend/`.
- Cấu hình TypeScript, Vite, Tailwind CSS.
- Cấu hình Spring Boot 3 + Maven.
- Cấu hình `application.yml`.
- Tạo health check endpoint.
- Tạo file hướng dẫn môi trường.

Ngoài scope:

- Chưa làm authentication.
- Chưa kết nối đầy đủ database domain.
- Chưa build UI thật.

## Files/Changes

| Khu vực | File/thư mục |
|---|---|
| Root | `README.md` |
| Root | `.gitignore` |
| Frontend | `frontend/.env.example` |
| Backend | `backend/.env.example` |
| Frontend | `frontend/package.json` |
| Frontend | `frontend/vite.config.ts` |
| Frontend | `frontend/tsconfig.json` |
| Frontend | `frontend/tailwind.config.js` |
| Frontend | `frontend/src/main.tsx` |
| Frontend | `frontend/src/App.tsx` |
| Frontend | `frontend/src/routes/AppRoutes.tsx` |
| Backend | `backend/pom.xml` |
| Backend | `backend/src/main/java/com/opportunityboard/OpportunityBoardApplication.java` |
| Backend | `backend/src/main/java/com/opportunityboard/controller/HealthController.java` |
| Backend | `backend/src/main/resources/application.yml` |
| Backend | `backend/src/main/resources/application-dev.yml` |

## Steps

1. Tạo frontend bằng React + TypeScript + Vite.
2. Cài Tailwind CSS và cấu hình style nền.
3. Tạo cấu trúc frontend rỗng theo hybrid feature-based structure.
4. Tạo backend Spring Boot 3 bằng Maven.
5. Thêm dependencies nền: Spring Web, Validation, Spring Data JPA, PostgreSQL Driver, Spring Security, Swagger/OpenAPI.
6. Tạo package gốc `com.opportunityboard`.
7. Tạo health check endpoint `/api/health`.
8. Tạo `frontend/.env.example` và `backend/.env.example` cho biến môi trường riêng của từng app.
9. Viết `README.md` hướng dẫn chạy local.

## Completion Criteria

- Chạy được frontend bằng `npm run dev`.
- Chạy được backend bằng Maven/Spring Boot.
- `GET /api/health` trả về OK.
- Frontend mở được màn hình placeholder.
- `README.md` có hướng dẫn chạy frontend và backend.
- `frontend/.env.example` có biến `VITE_API_BASE_URL`.
- `backend/.env.example` có biến cho database, JWT, Cloudinary, SMTP.

## Risks & Checks

| Rủi ro | Cách kiểm tra |
|---|---|
| Frontend/backend dùng port trùng nhau | Chạy cả hai cùng lúc |
| CORS chưa chuẩn | Tạm cấu hình cho frontend localhost |
| Dependency Spring Boot thiếu | Chạy backend và kiểm tra startup log |
