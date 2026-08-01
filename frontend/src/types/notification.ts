export type NotificationType =
  | "DEADLINE_REMINDER"
  | "APPLICATION_STATUS"
  | "OPPORTUNITY_APPROVED"
  | "OPPORTUNITY_REJECTED"
  | "WEEKLY_DIGEST"
  | "SYSTEM";

export type NotificationItem = {
  id: string;
  type: NotificationType;
  title: string;
  message: string;
  actionUrl: string | null;
  read: boolean;
  readAt: string | null;
  createdAt: string;
};

export type UnreadNotificationCount = {
  count: number;
};
