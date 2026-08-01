# Phase 10 - Notifications và Scheduled Jobs

## Overview

Phase này xây dựng notification trong hệ thống, gửi email và các job tự động như deadline reminder và weekly digest.

## Scope

Trong scope:

- Notification entity/service.
- Gửi email bằng SMTP.
- Email khi application status thay đổi.
- Deadline reminder.
- Weekly digest cơ bản.

Ngoài scope:

- Chưa làm push notification.
- Chưa làm notification realtime bằng WebSocket.

## Files/Changes

| Khu vực | File/thư mục |
|---|---|
| Backend entity | `backend/src/main/java/com/opportunityboard/entity/Notification.java` |
| Backend service | `backend/src/main/java/com/opportunityboard/service/notification/NotificationService.java` |
| Backend service impl | `backend/src/main/java/com/opportunityboard/service/notification/impl/NotificationServiceImpl.java` |
| Backend infrastructure | `backend/src/main/java/com/opportunityboard/infrastructure/mail/MailService.java` |
| Backend infrastructure | `backend/src/main/java/com/opportunityboard/infrastructure/mail/SmtpMailService.java` |
| Backend infrastructure | `backend/src/main/java/com/opportunityboard/infrastructure/template/EmailTemplateService.java` |
| Backend scheduler | `backend/src/main/java/com/opportunityboard/scheduler/DeadlineReminderScheduler.java` |
| Backend scheduler | `backend/src/main/java/com/opportunityboard/scheduler/WeeklyDigestScheduler.java` |
| Frontend feature | `frontend/src/features/notifications/components/*` |
| Frontend API | `frontend/src/features/notifications/api/notificationApi.ts` |

## Completion Criteria

- SMTP gửi được email trong môi trường dev.
- Khi application status đổi, student nhận notification/email.
- Scheduler tìm đúng opportunity sắp hết hạn.
- Không gửi trùng reminder cho cùng user/opportunity trong cùng mốc nhắc.
- Weekly digest gửi danh sách opportunity mới/phù hợp cơ bản.
- Notification được lưu để hiển thị trong dashboard.

## Risks & Checks

| Rủi ro | Cách kiểm tra |
|---|---|
| Email bị gửi trùng | Lưu log/mốc reminder và test scheduler |
| SMTP lỗi làm hỏng transaction chính | Gửi email async hoặc xử lý lỗi riêng |
| Nội dung email thiếu thông tin | Manual QA email template |
