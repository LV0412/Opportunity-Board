import { useEffect, useState } from "react";
import { ROUTES } from "../../config/routes";
import { adminApi } from "../../features/admin/api/adminApi";
import type { UserStatus } from "../../types/auth";
import type { AdminUser } from "../../types/admin";

const statuses: UserStatus[] = ["ACTIVE", "LOCKED", "DISABLED"];

export function UsersPage() {
  const [items, setItems] = useState<AdminUser[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    adminApi.listUsers().then((response) => setItems(response.content)).catch((exception) => {
      setError(exception instanceof Error ? exception.message : "Không thể tải người dùng");
    });
  }, []);

  async function updateStatus(id: string, status: UserStatus) {
    const updated = await adminApi.updateUserStatus(id, status);
    setItems((current) => current.map((item) => item.id === id ? updated : item));
  }

  return (
    <main className="min-h-screen bg-background text-foreground">
      <section className="mx-auto max-w-6xl px-6 py-10">
        <a className="text-sm font-semibold text-primary" href={ROUTES.adminDashboard}>Về dashboard</a>
        <h1 className="mt-4 text-3xl font-bold">Quản lý người dùng</h1>
        <p className="mt-2 text-muted-foreground">Khóa, mở khóa hoặc vô hiệu hóa tài khoản khi cần.</p>
        {error ? <p className="mt-6 rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p> : null}
        <div className="mt-8 overflow-hidden rounded-md border border-border bg-white shadow-sm">
          <div className="hidden grid-cols-[1.2fr_1fr_140px_160px] gap-4 border-b border-border bg-muted px-4 py-3 text-sm font-semibold text-muted-foreground md:grid">
            <span>Người dùng</span>
            <span>Vai trò</span>
            <span>Ngày tạo</span>
            <span>Trạng thái</span>
          </div>
          {items.map((item) => (
            <div key={item.id} className="grid gap-4 border-b border-border px-4 py-4 text-sm last:border-b-0 md:grid-cols-[1.2fr_1fr_140px_160px]">
              <div>
                <p className="font-semibold">{item.fullName}</p>
                <p className="mt-1 text-muted-foreground">{item.email}</p>
              </div>
              <div className="font-semibold">{item.role}</div>
              <div className="text-muted-foreground">{new Date(item.createdAt).toLocaleDateString("vi-VN")}</div>
              <select
                className="h-10 w-full rounded-md border border-border bg-background px-3 text-sm font-semibold outline-none focus:border-primary"
                value={item.status}
                onChange={(event) => void updateStatus(item.id, event.target.value as UserStatus)}
              >
                {statuses.map((status) => (
                  <option key={status} value={status}>{status}</option>
                ))}
              </select>
            </div>
          ))}
          {!items.length ? <p className="p-5 text-sm text-muted-foreground">Chưa có người dùng.</p> : null}
        </div>
      </section>
    </main>
  );
}
