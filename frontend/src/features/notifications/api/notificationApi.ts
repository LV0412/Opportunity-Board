import { apiClient } from "../../../config/apiClient";
import type { PageResponse } from "../../../types/opportunity";
import type { NotificationItem, UnreadNotificationCount } from "../../../types/notification";

export const notificationApi = {
  listMine(page = 0, size = 6) {
    return apiClient<PageResponse<NotificationItem>>(`/notifications/me?page=${page}&size=${size}`);
  },
  unreadCount() {
    return apiClient<UnreadNotificationCount>("/notifications/me/unread-count");
  },
  markAsRead(id: string) {
    return apiClient<NotificationItem>(`/notifications/${id}/read`, {
      method: "PATCH",
    });
  },
  markAllAsRead() {
    return apiClient<void>("/notifications/me/read-all", {
      method: "POST",
    });
  },
};
