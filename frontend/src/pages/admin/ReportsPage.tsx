import { useEffect, useState } from "react";
import { ROUTES } from "../../config/routes";
import { adminApi } from "../../features/admin/api/adminApi";
import type { AdminReport, AdminReportStatus } from "../../types/admin";

const statuses: Array<{ value: AdminReportStatus; label: string }> = [
  { value: "PENDING", label: "Chờ xử lý" },
  { value: "RESOLVED", label: "Đã xử lý" },
  { value: "REJECTED", label: "Bỏ qua" },
];

export function ReportsPage() {
  const [items, setItems] = useState<AdminReport[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    adminApi.listReports().then((response) => setItems(response.content)).catch((exception) => {
      setError(exception instanceof Error ? exception.message : "Không thể tải reports");
    });
  }, []);

  async function updateStatus(id: string, status: AdminReportStatus) {
    const updated = await adminApi.updateReportStatus(id, status);
    setItems((current) => current.map((item) => item.id === id ? updated : item));
  }

  return (
    <main className="min-h-screen bg-background text-foreground">
      <section className="mx-auto max-w-6xl px-6 py-10">
        <a className="text-sm font-semibold text-primary" href={ROUTES.adminDashboard}>Về dashboard</a>
        <h1 className="mt-4 text-3xl font-bold">Xử lý reports</h1>
        <p className="mt-2 text-muted-foreground">Kiểm tra các báo cáo từ sinh viên và cập nhật trạng thái xử lý.</p>
        {error ? <p className="mt-6 rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p> : null}
        <div className="mt-8 space-y-4">
          {items.map((item) => (
            <article key={item.id} className="rounded-md border border-border bg-white p-5 shadow-sm">
              <div className="grid gap-4 md:grid-cols-[1.5fr_200px]">
                <div>
                  <h2 className="font-semibold">{item.opportunityTitle}</h2>
                  <p className="mt-1 text-sm text-muted-foreground">{item.reporterName} · {item.reporterEmail}</p>
                  <p className="mt-3 text-sm font-semibold text-foreground">Lý do: {item.reason}</p>
                  {item.description ? <p className="mt-2 text-sm leading-6 text-muted-foreground">{item.description}</p> : null}
                </div>
                <div className="space-y-3">
                  <p className="text-sm text-muted-foreground">{new Date(item.createdAt).toLocaleString("vi-VN")}</p>
                  <select
                    className="h-10 w-full rounded-md border border-border bg-background px-3 text-sm font-semibold outline-none focus:border-primary"
                    value={item.status}
                    onChange={(event) => void updateStatus(item.id, event.target.value as AdminReportStatus)}
                  >
                    {statuses.map((status) => (
                      <option key={status.value} value={status.value}>{status.label}</option>
                    ))}
                  </select>
                </div>
              </div>
            </article>
          ))}
          {!items.length ? <p className="rounded-md border border-border bg-white p-5 text-sm text-muted-foreground">Chưa có report nào.</p> : null}
        </div>
      </section>
    </main>
  );
}
