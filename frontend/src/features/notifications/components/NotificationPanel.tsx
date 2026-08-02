import { Bell, CheckCheck, ChevronRight, Clock3 } from "lucide-react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { notificationApi } from "../api/notificationApi";
import type { NotificationItem } from "../../../types/notification";

type NotificationPanelProps = {
  title?: string;
  pageSize?: number;
};

export function NotificationPanel({ title = "Thông báo", pageSize = 6 }: NotificationPanelProps) {
  const [items, setItems] = useState<NotificationItem[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [submittingId, setSubmittingId] = useState<string | null>(null);
  const [error, setError] = useState("");

  const loadNotifications = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const [result, unread] = await Promise.all([
        notificationApi.listMine(0, pageSize),
        notificationApi.unreadCount(),
      ]);
      setItems(result.content);
      setUnreadCount(unread.count);
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Không thể tải thông báo");
    } finally {
      setLoading(false);
    }
  }, [pageSize]);

  useEffect(() => {
    void loadNotifications();
  }, [loadNotifications]);

  async function markAsRead(notificationId: string) {
    setSubmittingId(notificationId);
    try {
      const updated = await notificationApi.markAsRead(notificationId);
      setItems((current) => current.map((item) => item.id === notificationId ? updated : item));
      setUnreadCount((current) => Math.max(current - 1, 0));
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Không thể cập nhật thông báo");
    } finally {
      setSubmittingId(null);
    }
  }

  async function markAllAsRead() {
    setSubmittingId("all");
    try {
      await notificationApi.markAllAsRead();
      setItems((current) => current.map((item) => ({ ...item, read: true, readAt: item.readAt ?? new Date().toISOString() })));
      setUnreadCount(0);
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Không thể cập nhật thông báo");
    } finally {
      setSubmittingId(null);
    }
  }

  const hasUnread = unreadCount > 0;
  const content = useMemo(() => items.slice(0, pageSize), [items, pageSize]);

  return (
    <section className="rounded-md border border-border bg-white p-5 shadow-sm">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-3">
          <span className="grid h-10 w-10 place-items-center rounded-md bg-primary/10 text-primary">
            <Bell className="h-5 w-5" aria-hidden="true" />
          </span>
          <div>
            <h2 className="font-semibold">{title}</h2>
            <p className="text-sm text-muted-foreground">
              {hasUnread ? `${unreadCount} thông báo chưa đọc` : "Bạn đã đọc hết thông báo"}
            </p>
          </div>
        </div>
        <button
          className="inline-flex h-10 items-center gap-2 rounded-md border border-border px-3 text-sm font-semibold text-foreground disabled:cursor-not-allowed disabled:opacity-50"
          type="button"
          disabled={!hasUnread || submittingId === "all"}
          onClick={() => void markAllAsRead()}
        >
          <CheckCheck className="h-4 w-4" aria-hidden="true" />
          Đánh dấu đã đọc
        </button>
      </div>

      {error ? <p className="mt-4 rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p> : null}

      {loading ? (
        <div className="mt-4 space-y-3">
          {Array.from({ length: Math.min(pageSize, 3) }).map((_, index) => (
            <div key={index} className="h-20 animate-pulse rounded-md border border-border bg-muted/40" />
          ))}
        </div>
      ) : null}

      {!loading && content.length ? (
        <div className="mt-4 space-y-3">
          {content.map((item) => (
            <article
              key={item.id}
              className={`rounded-md border px-4 py-3 transition ${item.read ? "border-border bg-background" : "border-primary/30 bg-primary/5"}`}
            >
              <div className="flex flex-wrap items-start justify-between gap-3">
                <div className="min-w-0 flex-1">
                  <div className="flex items-center gap-2">
                    {!item.read ? <span className="h-2.5 w-2.5 rounded-full bg-primary" aria-hidden="true" /> : null}
                    <h3 className="font-semibold">{item.title}</h3>
                  </div>
                  <p className="mt-2 text-sm text-muted-foreground">{item.message}</p>
                  <p className="mt-3 inline-flex items-center gap-2 text-xs font-medium text-muted-foreground">
                    <Clock3 className="h-3.5 w-3.5" aria-hidden="true" />
                    {new Date(item.createdAt).toLocaleString("vi-VN")}
                  </p>
                </div>
                <div className="flex items-center gap-2">
                  {item.actionUrl ? (
                    <a
                      className="inline-flex h-9 items-center gap-2 rounded-md border border-border px-3 text-sm font-semibold"
                      href={item.actionUrl}
                      onClick={() => {
                        if (!item.read) {
                          void markAsRead(item.id);
                        }
                      }}
                    >
                      Mở
                      <ChevronRight className="h-4 w-4" aria-hidden="true" />
                    </a>
                  ) : null}
                  {!item.read ? (
                    <button
                      className="h-9 rounded-md bg-primary px-3 text-sm font-semibold text-primary-foreground disabled:cursor-not-allowed disabled:opacity-50"
                      type="button"
                      disabled={submittingId === item.id}
                      onClick={() => void markAsRead(item.id)}
                    >
                      Đã đọc
                    </button>
                  ) : null}
                </div>
              </div>
            </article>
          ))}
        </div>
      ) : null}

      {!loading && !content.length ? (
        <div className="mt-4 rounded-md border border-dashed border-border px-4 py-8 text-center">
          <p className="font-semibold">Chưa có thông báo nào</p>
          <p className="mt-2 text-sm text-muted-foreground">Thông báo về ứng tuyển, deadline và tổng hợp tuần sẽ hiển thị tại đây.</p>
        </div>
      ) : null}
    </section>
  );
}
