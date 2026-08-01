# Phase 14 - Deployment Preparation

## Overview

Phase này chuẩn bị đưa MVP lên môi trường staging/production với frontend trên Vercel, backend trên Render/Railway và database trên Neon PostgreSQL.

## Scope

Trong scope:

- Cấu hình deploy frontend.
- Cấu hình deploy backend.
- Cấu hình production env.
- Kiểm tra CORS.
- Kiểm tra JWT secret.
- Kiểm tra SMTP và Cloudinary.
- Cập nhật tài liệu chạy/deploy.

Ngoài scope:

- Chưa cần Kubernetes.
- Chưa cần CI/CD phức tạp.
- Chưa cần autoscaling nâng cao.

## Files/Changes

| Khu vực | File/thư mục |
|---|---|
| Frontend deploy | `frontend/vercel.json` nếu cần |
| Backend deploy | `backend/Dockerfile` hoặc cấu hình Render/Railway |
| Backend config | `backend/src/main/resources/application-prod.yml` |
| Root docs | `README.md` |
| Env docs | `frontend/.env.example`, `backend/.env.example` |

## Environment Variables

Các biến cần có:

- `DATABASE_URL`
- `JWT_SECRET`
- `JWT_EXPIRATION`
- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`
- `SMTP_HOST`
- `SMTP_PORT`
- `SMTP_USERNAME`
- `SMTP_PASSWORD`
- `FRONTEND_URL`
- `BACKEND_URL`

## Completion Criteria

- Frontend deploy được lên Vercel.
- Backend deploy được lên Render hoặc Railway.
- Backend kết nối được Neon PostgreSQL.
- Frontend gọi được backend production.
- CORS chỉ cho phép domain frontend hợp lệ.
- Swagger production truy cập được nếu bật public docs.
- README có hướng dẫn deploy cơ bản.

## Risks & Checks

| Rủi ro | Cách kiểm tra |
|---|---|
| CORS sai | Test login từ domain frontend production |
| Secret bị commit | Kiểm tra `.env` không nằm trong git |
| Backend sleep cold start | Ghi chú hạn chế của Render/Railway free tier |
