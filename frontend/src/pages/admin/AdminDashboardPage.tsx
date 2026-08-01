import { FileText, ShieldCheck, Siren, Users } from "lucide-react";
import { useEffect, useState } from "react";
import { ROUTES } from "../../config/routes";
import { useAuth } from "../../features/auth/hooks/useAuth";
import { dashboardApi } from "../../features/dashboard/api/dashboardApi";
import { AdminLayout } from "../../layouts/AdminLayout";
import type { AdminDashboard } from "../../types/dashboard";

export function AdminDashboardPage() {
  const { user, logout } = useAuth();
  const [dashboard, setDashboard] = useState<AdminDashboard | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    setLoading(true);
    dashboardApi.getAdminDashboard()
      .then(setDashboard)
      .catch((exception) => {
        setError(exception instanceof Error ? exception.message : "Khong the tai dashboard");
        setDashboard(null);
      })
      .finally(() => setLoading(false));
  }, []);

  return (
    <AdminLayout
      title="Admin Dashboard"
      subtitle={`Xin chào, ${user?.fullName ?? ""}`}
      onLogout={logout}
      actions={(
        <a className="rounded-md bg-primary px-4 py-2.5 text-sm font-semibold text-primary-foreground" href={ROUTES.adminPendingOpportunities}>
          Duyệt cơ hội
        </a>
      )}
    >
      {error ? <p className="rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p> : null}

      {loading ? (
        <div className="grid gap-4 lg:grid-cols-3">
          {Array.from({ length: 6 }).map((_, index) => (
            <div key={index} className="h-32 animate-pulse rounded-md border border-border bg-white" />
          ))}
        </div>
      ) : null}

      {dashboard ? (
        <div className="space-y-6">
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
            <MetricCard icon={ShieldCheck} label="Chờ duyệt" value={dashboard.pendingOpportunities} hint="Cơ hội đang chờ moderation" />
            <MetricCard icon={Siren} label="Reports" value={dashboard.pendingReports} hint="Báo cáo chưa xử lý" />
            <MetricCard icon={Users} label="Người dùng" value={dashboard.totalUsers} hint={`${dashboard.totalStudents} sinh viên, ${dashboard.totalOrganizations} tổ chức`} />
            <MetricCard icon={FileText} label="Ứng tuyển" value={dashboard.totalApplications} hint={`${dashboard.totalOpportunities} cơ hội trong hệ thống`} />
          </div>

          <div className="grid gap-6 xl:grid-cols-2">
            <section className="rounded-md border border-border bg-white p-5 shadow-sm">
              <div className="flex items-center justify-between gap-3">
                <div>
                  <h2 className="text-lg font-semibold">Cơ hội chờ duyệt</h2>
                  <p className="mt-1 text-sm text-muted-foreground">Cần admin review sớm để tránh tồn đọng.</p>
                </div>
                <a className="text-sm font-semibold text-primary" href={ROUTES.adminPendingOpportunities}>Mở moderation</a>
              </div>
              {dashboard.recentPendingOpportunities.length ? (
                <div className="mt-5 space-y-3">
                  {dashboard.recentPendingOpportunities.map((opportunity) => (
                    <article key={opportunity.id} className="rounded-md border border-border bg-background p-4">
                      <div className="flex items-start justify-between gap-3">
                        <div>
                          <a className="font-semibold hover:text-primary" href={`${ROUTES.opportunityDetail}/${opportunity.id}`}>{opportunity.title}</a>
                          <p className="mt-1 text-sm text-muted-foreground">{opportunity.organizationName}</p>
                        </div>
                        <span className="rounded-md bg-primary/10 px-2 py-1 text-xs font-semibold text-primary">{opportunity.status}</span>
                      </div>
                      <p className="mt-3 text-sm text-muted-foreground">Tạo ngày {new Date(opportunity.createdAt).toLocaleDateString("vi-VN")}</p>
                    </article>
                  ))}
                </div>
              ) : (
                <EmptyState title="Không có bài chờ duyệt" description="Hàng đợi moderation hiện đang trống." />
              )}
            </section>

            <section className="rounded-md border border-border bg-white p-5 shadow-sm">
              <div className="flex items-center justify-between gap-3">
                <div>
                  <h2 className="text-lg font-semibold">Reports mới</h2>
                  <p className="mt-1 text-sm text-muted-foreground">Danh sách báo cáo cần xem trước.</p>
                </div>
                <a className="text-sm font-semibold text-primary" href={ROUTES.adminReports}>Mở reports</a>
              </div>
              {dashboard.recentReports.length ? (
                <div className="mt-5 space-y-3">
                  {dashboard.recentReports.map((report) => (
                    <article key={report.id} className="rounded-md border border-border bg-background p-4">
                      <div className="flex items-start justify-between gap-3">
                        <div>
                          <p className="font-semibold">{report.opportunityTitle}</p>
                          <p className="mt-1 text-sm text-muted-foreground">{report.reason}</p>
                        </div>
                        <span className="rounded-md bg-primary/10 px-2 py-1 text-xs font-semibold text-primary">{report.status}</span>
                      </div>
                      <p className="mt-3 text-sm text-muted-foreground">Người báo cáo: {report.reporterName}</p>
                    </article>
                  ))}
                </div>
              ) : (
                <EmptyState title="Không có report mới" description="Không có báo cáo pending trong hệ thống." />
              )}
            </section>
          </div>

          <div className="flex flex-wrap gap-3">
            <a className="rounded-md border border-border bg-white px-4 py-2.5 text-sm font-semibold" href={ROUTES.adminUsers}>Quản lý người dùng</a>
            <a className="rounded-md border border-border bg-white px-4 py-2.5 text-sm font-semibold" href={ROUTES.adminCategories}>Quản lý categories và tags</a>
          </div>
        </div>
      ) : null}
    </AdminLayout>
  );
}

function MetricCard({
  icon: Icon,
  label,
  value,
  hint,
}: {
  icon: typeof ShieldCheck;
  label: string;
  value: number;
  hint: string;
}) {
  return (
    <article className="rounded-md border border-border bg-white p-5 shadow-sm">
      <Icon className="h-5 w-5 text-primary" aria-hidden="true" />
      <p className="mt-4 text-sm font-semibold text-muted-foreground">{label}</p>
      <p className="mt-2 text-3xl font-bold">{value}</p>
      <p className="mt-2 text-sm text-muted-foreground">{hint}</p>
    </article>
  );
}

function EmptyState({ title, description }: { title: string; description: string }) {
  return (
    <div className="mt-5 rounded-md border border-dashed border-border px-4 py-8 text-center">
      <h3 className="font-semibold">{title}</h3>
      <p className="mt-2 text-sm text-muted-foreground">{description}</p>
    </div>
  );
}

export default AdminDashboardPage;
