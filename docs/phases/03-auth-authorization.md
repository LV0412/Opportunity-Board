# Phase 03 - Authentication và Authorization

## Overview

Phase này xây dựng đăng ký, đăng nhập, JWT authentication và phân quyền theo 3 role: `STUDENT`, `ORGANIZATION`, `ADMIN`.

## Scope

Trong scope:

- Register/login bằng email/password.
- Hash password.
- JWT access token.
- Endpoint lấy user hiện tại.
- Route guard frontend.
- Role-based access backend.

Ngoài scope:

- Chưa làm OAuth.
- Chưa làm forgot password.
- Chưa làm email verification bắt buộc.

## Files/Changes

| Khu vực | File/thư mục |
|---|---|
| Backend config | `backend/src/main/java/com/opportunityboard/config/SecurityConfig.java` |
| Backend security | `backend/src/main/java/com/opportunityboard/security/JwtService.java` |
| Backend security | `backend/src/main/java/com/opportunityboard/security/JwtAuthenticationFilter.java` |
| Backend security | `backend/src/main/java/com/opportunityboard/security/CustomUserDetails.java` |
| Backend security | `backend/src/main/java/com/opportunityboard/security/CustomUserDetailsService.java` |
| Backend controller | `backend/src/main/java/com/opportunityboard/controller/AuthController.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/auth/AuthService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/auth/impl/AuthServiceImpl.java` |
| Backend DTO request | `backend/src/main/java/com/opportunityboard/dto/request/auth/RegisterRequest.java` |
| Backend DTO request | `backend/src/main/java/com/opportunityboard/dto/request/auth/LoginRequest.java` |
| Backend DTO response | `backend/src/main/java/com/opportunityboard/dto/response/auth/AuthResponse.java` |
| Frontend auth | `frontend/src/features/auth/components/*` |
| Frontend auth | `frontend/src/features/auth/hooks/useAuth.ts` |
| Frontend auth | `frontend/src/features/auth/api/authApi.ts` |
| Frontend pages | `frontend/src/pages/auth/LoginPage.tsx` |
| Frontend pages | `frontend/src/pages/auth/RegisterPage.tsx` |
| Frontend context | `frontend/src/context/AuthContext.tsx` |
| Frontend routes | `frontend/src/routes/ProtectedRoute.tsx` |
| Frontend routes | `frontend/src/routes/RoleBasedRoute.tsx` |

## APIs

| Method | Endpoint | Quyền |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| GET | `/api/auth/me` | Authenticated |
| POST | `/api/auth/refresh-token` | Authenticated |

## Completion Criteria

- Student, Organization, Admin đăng ký/đăng nhập được.
- Password được hash an toàn.
- JWT được trả về sau khi login.
- Frontend lưu token và gửi kèm request.
- User đăng nhập xong được điều hướng về dashboard đúng role.
- Student token không gọi được API organization/admin.
- Organization token không gọi được API admin.

## Risks & Checks

| Rủi ro | Cách kiểm tra |
|---|---|
| Lộ endpoint admin | Integration test role-based access |
| Token hết hạn không xử lý tốt | Test flow refresh token hoặc logout |
| User bị khóa vẫn thao tác được | Check `UserStatus` trong auth filter/service |
