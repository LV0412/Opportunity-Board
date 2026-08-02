import { Bell, BriefcaseBusiness, ClipboardCheck, Eye, PlusCircle, Users } from "lucide-react";
import { useEffect, useState } from "react";
import { ROUTES } from "../../config/routes";
import { useAuth } from "../../features/auth/hooks/useAuth";
import { dashboardApi } from "../../features/dashboard/api/dashboardApi";
import { NotificationPanel } from "../../features/notifications/components/NotificationPanel";
import { DashboardLayout } from "../../layouts/DashboardLayout";
import type { OrganizationDashboard } from "../../types/dashboard";

export function OrganizationDashboardPage() {
  const { user, logout } = useAuth();
  const [dashboard, setDashboard] = useState<OrganizationDashboard | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    setLoading(true);
    dashboardApi.getOrganizationDashboard()
      .then(setDashboard)
      .catch((exception) => {
        setError(exception instanceof Error ? exception.message : "Khong the tai dashboard");
        setDashboard(null);
      })
      .finally(() => setLoading(false));
  }, []);

  return (
    <DashboardLayout
      role="ORGANIZATION"
      title="Organization Dashboard"
      subtitle={`Xin chào, ${user?.fullName ?? ""}`}
      onLogout={logout}
      actions={(
        <>
          <a className="rounded-md bg-primary px-4 py-2.5 font-semibold text-primary-foreground" href={ROUTES.organizationProfile}>
            Cập nhật hồ sơ
          </a>
          <a className="rounded-md border border-border bg-white px-4 py-2.5 font-semibold text-foreground" href={ROUTES.organizationOpportunities}>
            Quản lý cơ hội
          </a>
          <a className="rounded-md border border-border bg-white px-4 py-2.5 font-semibold text-foreground" href={ROUTES.organizationApplicants}>
            Xem ứng viên
          </a>
        </>
      )}
      secondary={<NotificationPanel title="Thông báo tổ chức" />}
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
          <div className="grid overflow-hidden rounded-md border border-outline-variant bg-white sm:grid-cols-2 xl:grid-cols-3 xl:divide-x xl:divide-outline-variant">
            <MetricCard icon={PlusCircle} label="Bài đăng" value={dashboard.totalOpportunities} hint="Tổng cơ hội đã tạo" />
            <MetricCard icon={ClipboardCheck} label="Chờ duyệt" value={dashboard.pendingOpportunities} hint="Đang chờ admin review" />
            <MetricCard icon={BriefcaseBusiness} label="Đã duyệt" value={dashboard.approvedOpportunities} hint="Đang hiển thị công khai" />
            <MetricCard icon={Eye} label="Lượt xem" value={dashboard.totalViews} hint="Tổng view của các cơ hội" />
            <MetricCard icon={Bell} label="Lượt lưu" value={dashboard.totalBookmarks} hint="Sinh viên đã bookmark" />
            <MetricCard icon={Users} label="Ứng tuyển" value={dashboard.totalApplications} hint="Tổng hồ sơ đã nộp" />
          </div>

          <section className="rounded-md border border-border bg-white p-5 shadow-sm">
            <div className="flex items-center justify-between gap-3">
              <div>
                <h2 className="text-lg font-semibold">Hiệu suất bài đăng</h2>
                <p className="mt-1 text-sm text-muted-foreground">Theo dõi nhanh lượt xem, lưu và ứng tuyển của các cơ hội mới nhất.</p>
              </div>
              <a className="text-sm font-semibold text-primary" href={ROUTES.organizationOpportunities}>Mở quản lý cơ hội</a>
            </div>

            {dashboard.recentOpportunities.length ? (
              <div className="mt-5 overflow-hidden rounded-md border border-border">
                <div className="hidden grid-cols-[1.3fr_120px_120px_120px_120px] gap-4 border-b border-border bg-muted px-4 py-3 text-sm font-semibold text-muted-foreground md:grid">
                  <span>Cơ hội</span>
                  <span>Lượt xem</span>
                  <span>Lượt lưu</span>
                  <span>Ứng tuyển</span>
                  <span>Trạng thái</span>
                </div>
                {dashboard.recentOpportunities.map((item) => (
                  <div key={item.opportunityId} className="grid gap-4 border-b border-border px-4 py-4 last:border-b-0 md:grid-cols-[1.3fr_120px_120px_120px_120px]">
                    <div>
                      <a className="font-semibold hover:text-primary" href={`${ROUTES.opportunityDetail}/${item.opportunityId}`}>{item.title}</a>
                      <p className="mt-1 text-sm text-muted-foreground">
                        {item.deadlineAt ? `Deadline ${new Date(item.deadlineAt).toLocaleDateString("vi-VN")}` : "Không có deadline"}
                      </p>
                    </div>
                    <MetricPill label="Lượt xem" value={item.viewCount} />
                    <MetricPill label="Lượt lưu" value={item.bookmarkCount} />
                    <MetricPill label="Ứng tuyển" value={item.applicationCount} />
                    <div>
                      <p className="text-xs font-semibold uppercase tracking-wide text-primary md:hidden">Trạng thái</p>
                      <p className="font-semibold">{item.status}</p>
                      <p className="mt-1 text-sm text-muted-foreground">Cập nhật {new Date(item.updatedAt).toLocaleDateString("vi-VN")}</p>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <EmptyState title="Chưa có bài đăng nào" description="Tạo cơ hội đầu tiên để bắt đầu thu hút ứng viên." />
            )}
          </section>
        </div>
      ) : null}
    </DashboardLayout>
  );
}

function MetricCard({
  icon: Icon,
  label,
  value,
  hint,
}: {
  icon: typeof PlusCircle;
  label: string;
  value: number;
  hint: string;
}) {
  return (
    <article className="border-b border-outline-variant bg-white p-5 last:border-b-0 xl:border-b-0">
      <Icon className="h-5 w-5 text-primary" aria-hidden="true" />
      <p className="mt-4 text-sm font-semibold text-muted-foreground">{label}</p>
      <p className="mt-2 text-3xl font-bold">{value}</p>
      <p className="mt-2 text-sm text-muted-foreground">{hint}</p>
    </article>
  );
}

function MetricPill({ label, value }: { label: string; value: number }) {
  return (
    <div>
      <p className="text-xs font-semibold uppercase tracking-wide text-primary md:hidden">{label}</p>
      <p className="font-semibold">{value}</p>
    </div>
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

export default OrganizationDashboardPage;
