import { useEffect, useState } from "react";
import { Pencil, XCircle } from "lucide-react";
import { ROUTES } from "../../config/routes";
import { opportunityApi } from "../../features/opportunities/api/opportunityApi";
import { OpportunityForm } from "../../features/opportunities/components/OpportunityForm";
import type { Opportunity, OpportunityPayload } from "../../types/opportunity";
import { useAuth } from "../../features/auth/hooks/useAuth";
import { DashboardLayout } from "../../layouts/DashboardLayout";

export function OrganizationOpportunitiesPage() {
  const { logout } = useAuth();
  const [items, setItems] = useState<Opportunity[]>([]);
  const [editing, setEditing] = useState<Opportunity | undefined>();
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    load();
  }, []);

  async function load() {
    try {
      const response = await opportunityApi.listMine();
      setItems(response.content);
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Không thể tải danh sách cơ hội");
    }
  }

  async function handleSubmit(payload: OpportunityPayload) {
    if (editing) {
      await opportunityApi.update(editing.id, payload);
      setMessage("Đã cập nhật và gửi duyệt lại.");
      setEditing(undefined);
    } else {
      await opportunityApi.create(payload);
      setMessage("Đã tạo cơ hội và gửi admin duyệt.");
    }
    await load();
  }

  async function handleClose(id: string) {
    await opportunityApi.close(id);
    setMessage("Đã đóng cơ hội.");
    await load();
  }

  return (
    <DashboardLayout role="ORGANIZATION" title="Quản lý cơ hội" subtitle="Tạo, cập nhật và theo dõi trạng thái các bài đăng của tổ chức." onLogout={logout}>
      <section className="organization-page-body">
        <a className="text-sm font-semibold text-primary" href={ROUTES.organizationDashboard}>Về dashboard</a>
        <h1 className="mt-4 text-3xl font-bold">Quản lý cơ hội</h1>
        <p className="mt-2 text-muted-foreground">Tạo, sửa và đóng cơ hội của tổ chức. Bài mới luôn ở trạng thái chờ duyệt.</p>
        {message ? <p className="mt-6 rounded-md bg-emerald-50 px-4 py-3 text-sm text-emerald-700">{message}</p> : null}
        {error ? <p className="mt-6 rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p> : null}
        <div className="mt-8 grid gap-6 lg:grid-cols-[1fr_420px]">
          <div className="space-y-4">
            {items.map((item) => (
              <article key={item.id} className="rounded-md border border-border bg-white p-5 shadow-sm">
                <div className="flex flex-wrap items-start justify-between gap-3">
                  <div>
                    <h2 className="font-semibold">{item.title}</h2>
                    <p className="mt-1 text-sm text-muted-foreground">{item.categoryName} · {item.status}</p>
                    {item.latestReviewNote ? <p className="mt-2 text-sm text-red-700">{item.latestReviewNote}</p> : null}
                  </div>
                  <div className="flex gap-2">
                    <button className="rounded-md border border-border p-2" type="button" title="Sửa" onClick={() => setEditing(item)}>
                      <Pencil className="h-4 w-4" aria-hidden="true" />
                    </button>
                    <button className="rounded-md border border-border p-2" type="button" title="Đóng" onClick={() => void handleClose(item.id)}>
                      <XCircle className="h-4 w-4" aria-hidden="true" />
                    </button>
                  </div>
                </div>
              </article>
            ))}
            {!items.length ? <p className="rounded-md border border-border bg-white p-5 text-sm text-muted-foreground">Chưa có cơ hội nào.</p> : null}
          </div>
          <OpportunityForm initialValue={editing} onSubmit={handleSubmit} />
        </div>
      </section>
    </DashboardLayout>
  );
}

export default OrganizationOpportunitiesPage;
