import { useEffect, useState } from "react";
import { CheckCircle, XCircle } from "lucide-react";
import { ROUTES } from "../../config/routes";
import { opportunityApi } from "../../features/opportunities/api/opportunityApi";
import type { Opportunity } from "../../types/opportunity";
import { useAuth } from "../../features/auth/hooks/useAuth";
import { AdminLayout } from "../../layouts/AdminLayout";

export function PendingOpportunitiesPage() {
  const { logout } = useAuth();
  const [items, setItems] = useState<Opportunity[]>([]);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    load();
  }, []);

  async function load() {
    try {
      const response = await opportunityApi.listPending();
      setItems(response.content);
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Không thể tải bài chờ duyệt");
    }
  }

  async function approve(id: string) {
    setError("");
    try {
      await opportunityApi.approve(id);
      setItems((current) => current.filter((item) => item.id !== id));
      setMessage("Đã duyệt cơ hội.");
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Không thể duyệt cơ hội");
    }
  }

  async function reject(id: string) {
    const reason = window.prompt("Lý do từ chối");
    if (!reason?.trim()) {
      return;
    }
    await opportunityApi.reject(id, reason.trim());
    setMessage("Đã từ chối cơ hội.");
    await load();
  }

  return (
    <AdminLayout title="Cơ hội chờ duyệt" subtitle="Kiểm tra chất lượng bài đăng trước khi hiển thị công khai." onLogout={logout}>
      <section className="admin-page-body">
        <a className="text-sm font-semibold text-primary" href={ROUTES.adminDashboard}>Về dashboard</a>
        <h1 className="mt-4 text-3xl font-bold">Cơ hội chờ duyệt</h1>
        <p className="mt-2 text-muted-foreground">Duyệt hoặc từ chối bài đăng trước khi hiển thị công khai.</p>
        {message ? <p className="mt-6 rounded-md bg-emerald-50 px-4 py-3 text-sm text-emerald-700">{message}</p> : null}
        {error ? <p className="mt-6 rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p> : null}
        <div className="mt-8 space-y-4">
          {items.map((item) => (
            <article key={item.id} className="rounded-md border border-border bg-white p-5 shadow-sm">
              <div className="flex flex-wrap items-start justify-between gap-4">
                <div>
                  <p className="text-sm font-semibold text-primary">{item.categoryName}</p>
                  <h2 className="mt-1 text-xl font-semibold">{item.title}</h2>
                  <p className="mt-2 text-sm text-muted-foreground">{item.organizationName}</p>
                  <p className="mt-3 line-clamp-2 text-sm leading-6 text-muted-foreground">{item.description}</p>
                </div>
                <div className="flex gap-2">
                  <button className="inline-flex items-center gap-2 rounded-md bg-primary px-3 py-2 text-sm font-semibold text-primary-foreground" type="button" onClick={() => void approve(item.id)}>
                    <CheckCircle className="h-4 w-4" aria-hidden="true" />
                    Duyệt
                  </button>
                  <button className="inline-flex items-center gap-2 rounded-md border border-border bg-white px-3 py-2 text-sm font-semibold" type="button" onClick={() => void reject(item.id)}>
                    <XCircle className="h-4 w-4" aria-hidden="true" />
                    Từ chối
                  </button>
                </div>
              </div>
            </article>
          ))}
          {!items.length ? <p className="rounded-md border border-border bg-white p-5 text-sm text-muted-foreground">Không có bài chờ duyệt.</p> : null}
        </div>
      </section>
    </AdminLayout>
  );
}

export default PendingOpportunitiesPage;
