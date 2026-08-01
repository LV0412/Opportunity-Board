import { Bell, Bookmark, CalendarClock, FileText, Search } from "lucide-react";
import { useEffect, useState } from "react";
import { ROUTES } from "../../config/routes";
import { useAuth } from "../../features/auth/hooks/useAuth";
import { dashboardApi } from "../../features/dashboard/api/dashboardApi";
import { NotificationPanel } from "../../features/notifications/components/NotificationPanel";
import { OpportunityCard } from "../../features/opportunities/components/OpportunityCard";
import { DashboardLayout } from "../../layouts/DashboardLayout";
import type { StudentDashboard } from "../../types/dashboard";

export function StudentDashboardPage() {
  const { user, logout } = useAuth();
  const [dashboard, setDashboard] = useState<StudentDashboard | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    setLoading(true);
    dashboardApi.getStudentDashboard()
      .then(setDashboard)
      .catch((exception) => {
        setError(exception instanceof Error ? exception.message : "Khong the tai dashboard");
        setDashboard(null);
      })
      .finally(() => setLoading(false));
  }, []);

  return (
    <DashboardLayout
      role="STUDENT"
      title="Student Dashboard"
      subtitle={`Xin chao, ${user?.fullName ?? ""}`}
      onLogout={logout}
      actions={(
        <a className="rounded-md bg-primary px-4 py-2.5 text-sm font-semibold text-primary-foreground" href={ROUTES.studentProfile}>
          Cập nhật hồ sơ
        </a>
      )}
      secondary={<NotificationPanel title="Thông báo của bạn" />}
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
            <MetricCard icon={Search} label="Cơ hội đề xuất" value={dashboard.recommendedOpportunities.length} hint="Gợi ý theo hồ sơ của bạn" />
            <MetricCard icon={Bookmark} label="Đã lưu" value={dashboard.savedCount} hint="Cơ hội bạn đã bookmark" />
            <MetricCard icon={FileText} label="Ứng tuyển" value={dashboard.applicationCount} hint="Hồ sơ đã nộp trong hệ thống" />
            <MetricCard icon={Bell} label="Chưa đọc" value={dashboard.unreadNotificationCount} hint="Thông báo mới đang chờ" />
          </div>

          <div className="grid gap-6 xl:grid-cols-[minmax(0,1.2fr)_320px]">
            <section className="rounded-md border border-border bg-white p-5 shadow-sm">
              <div className="flex items-center justify-between gap-3">
                <div>
                  <h2 className="text-lg font-semibold">Cơ hội đề xuất</h2>
                  <p className="mt-1 text-sm text-muted-foreground">Rule-based theo major, interests và skills của bạn.</p>
                </div>
                <a className="text-sm font-semibold text-primary" href={ROUTES.explore}>Xem tất cả</a>
              </div>
              {dashboard.recommendedOpportunities.length ? (
                <div className="mt-5 grid gap-4 lg:grid-cols-2">
                  {dashboard.recommendedOpportunities.map((opportunity) => (
                    <OpportunityCard key={opportunity.id} opportunity={opportunity} />
                  ))}
                </div>
              ) : (
                <EmptyState title="Chưa có gợi ý phù hợp" description="Cập nhật hồ sơ học tập và kỹ năng để nhận đề xuất tốt hơn." />
              )}
            </section>

            <section className="rounded-md border border-border bg-white p-5 shadow-sm">
              <div className="flex items-start gap-3">
                <span className="grid h-11 w-11 place-items-center rounded-md bg-primary/10 text-primary">
                  <CalendarClock className="h-5 w-5" aria-hidden="true" />
                </span>
                <div>
                  <h2 className="text-lg font-semibold">Deadline gần nhất</h2>
                  <p className="mt-1 text-sm text-muted-foreground">Mốc thời gian quan trọng cần ưu tiên tiếp theo.</p>
                </div>
              </div>
              {dashboard.nearestDeadline ? (
                <div className="mt-5 rounded-md border border-border bg-background p-4">
                  <p className="text-xs font-semibold uppercase tracking-wide text-primary">
                    {dashboard.nearestDeadline.source === "APPLICATION" ? "Từ hồ sơ ứng tuyển" : "Từ danh sách đã lưu"}
                  </p>
                  <a className="mt-2 block font-semibold text-foreground hover:text-primary" href={`${ROUTES.opportunityDetail}/${dashboard.nearestDeadline.opportunityId}`}>
                    {dashboard.nearestDeadline.opportunityTitle}
                  </a>
                  <p className="mt-2 text-sm text-muted-foreground">{dashboard.nearestDeadline.organizationName}</p>
                  <p className="mt-3 text-sm font-semibold">
                    {new Date(dashboard.nearestDeadline.deadlineAt).toLocaleString("vi-VN")}
                  </p>
                </div>
              ) : (
                <EmptyState title="Chưa có deadline sắp tới" description="Hãy lưu hoặc ứng tuyển cơ hội để theo dõi mốc quan trọng tại đây." />
              )}
            </section>
          </div>

          <section className="rounded-md border border-border bg-white p-5 shadow-sm">
            <div className="flex items-center justify-between gap-3">
              <div>
                <h2 className="text-lg font-semibold">Ứng tuyển gần đây</h2>
                <p className="mt-1 text-sm text-muted-foreground">Các cập nhật mới nhất từ tổ chức tuyển dụng.</p>
              </div>
              <a className="text-sm font-semibold text-primary" href={ROUTES.studentApplications}>Mở tracker</a>
            </div>
            {dashboard.recentApplications.length ? (
              <div className="mt-5 grid gap-3 md:grid-cols-3">
                {dashboard.recentApplications.map((application) => (
                  <article key={application.id} className="rounded-md border border-border bg-background p-4">
                    <p className="text-xs font-semibold uppercase tracking-wide text-primary">{application.status}</p>
                    <h3 className="mt-2 font-semibold">{application.opportunityTitle}</h3>
                    <p className="mt-2 text-sm text-muted-foreground">{application.organizationName}</p>
                    <p className="mt-3 text-sm text-muted-foreground">
                      Cập nhật {new Date(application.updatedAt).toLocaleDateString("vi-VN")}
                    </p>
                  </article>
                ))}
              </div>
            ) : (
              <EmptyState title="Chưa có hồ sơ ứng tuyển" description="Ứng tuyển từ trang chi tiết cơ hội để bắt đầu theo dõi trạng thái." />
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
  icon: typeof Search;
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

export default StudentDashboardPage;
