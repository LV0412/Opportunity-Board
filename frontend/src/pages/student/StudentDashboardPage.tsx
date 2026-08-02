import { ArrowRight, Bell, Bookmark, CalendarClock, FileText, Search, Sparkles } from "lucide-react";
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
        setError(exception instanceof Error ? exception.message : "Không thể tải tổng quan");
        setDashboard(null);
      })
      .finally(() => setLoading(false));
  }, []);

  return (
    <DashboardLayout
      role="STUDENT"
      title="Tổng quan của bạn"
      subtitle={`Chào ${user?.fullName ?? "bạn"}, cùng xem những cơ hội đáng chú ý hôm nay.`}
      onLogout={logout}
      actions={<a className="dashboard-primary-action" href={ROUTES.studentProfile}>Cập nhật hồ sơ <ArrowRight aria-hidden="true" /></a>}
      secondary={<NotificationPanel title="Thông báo" pageSize={4} />}
    >
      {error ? <p className="dashboard-error">{error}</p> : null}

      {loading ? (
        <div className="student-dashboard-loading" aria-label="Đang tải dữ liệu">
          {Array.from({ length: 6 }).map((_, index) => <div key={index} />)}
        </div>
      ) : null}

      {dashboard ? (
        <div className="student-dashboard">
          <section className="student-welcome-card">
            <div>
              <span><Sparkles aria-hidden="true" /> Gợi ý dành riêng cho bạn</span>
              <h2>{dashboard.recommendedOpportunities.length} cơ hội đang chờ bạn khám phá</h2>
              <p>Hồ sơ càng đầy đủ, đề xuất càng sát với kỹ năng và định hướng của bạn.</p>
            </div>
            <a href={ROUTES.explore}>Khám phá ngay <ArrowRight aria-hidden="true" /></a>
          </section>

          <div className="student-metrics">
            <MetricCard icon={Search} label="Đề xuất" value={dashboard.recommendedOpportunities.length} hint="Phù hợp với hồ sơ" tone="teal" />
            <MetricCard icon={Bookmark} label="Đã lưu" value={dashboard.savedCount} hint="Cơ hội quan tâm" tone="gold" />
            <MetricCard icon={FileText} label="Ứng tuyển" value={dashboard.applicationCount} hint="Hồ sơ đã gửi" tone="coral" />
            <MetricCard icon={Bell} label="Chưa đọc" value={dashboard.unreadNotificationCount} hint="Thông báo mới" tone="violet" />
          </div>

          <div className="student-focus-grid">
            <section className="student-section student-opportunities">
              <SectionHeader title="Cơ hội nổi bật" description="Được chọn dựa trên chuyên ngành, kỹ năng và sở thích của bạn." href={ROUTES.explore} action="Xem tất cả" />
              {dashboard.recommendedOpportunities.length ? (
                <div className="student-opportunity-grid">
                  {dashboard.recommendedOpportunities.slice(0, 4).map((opportunity) => <OpportunityCard key={opportunity.id} opportunity={opportunity} />)}
                </div>
              ) : <EmptyState title="Chưa có gợi ý phù hợp" description="Cập nhật hồ sơ học tập và kỹ năng để nhận đề xuất tốt hơn." />}
            </section>

            <section className="student-section student-deadline-card">
              <div className="student-deadline-heading"><span><CalendarClock aria-hidden="true" /></span><div><small>ƯU TIÊN TIẾP THEO</small><h2>Deadline gần nhất</h2></div></div>
              {dashboard.nearestDeadline ? (
                <div className="student-deadline-content">
                  <span>{dashboard.nearestDeadline.source === "APPLICATION" ? "Đã ứng tuyển" : "Đã lưu"}</span>
                  <a href={`${ROUTES.opportunityDetail}/${dashboard.nearestDeadline.opportunityId}`}>{dashboard.nearestDeadline.opportunityTitle}</a>
                  <p>{dashboard.nearestDeadline.organizationName}</p>
                  <time>{new Date(dashboard.nearestDeadline.deadlineAt).toLocaleString("vi-VN")}</time>
                </div>
              ) : <EmptyState title="Chưa có deadline" description="Lưu hoặc ứng tuyển một cơ hội để theo dõi tại đây." />}
            </section>
          </div>

          <section className="student-section student-applications">
            <SectionHeader title="Ứng tuyển gần đây" description="Theo dõi những cập nhật mới nhất trong hành trình của bạn." href={ROUTES.studentApplications} action="Mở trình theo dõi" />
            {dashboard.recentApplications.length ? (
              <div className="student-application-list">
                {dashboard.recentApplications.map((application) => (
                  <article key={application.id}><span>{application.status}</span><div><h3>{application.opportunityTitle}</h3><p>{application.organizationName}</p></div><time>Cập nhật {new Date(application.updatedAt).toLocaleDateString("vi-VN")}</time></article>
                ))}
              </div>
            ) : <EmptyState title="Chưa có hồ sơ ứng tuyển" description="Ứng tuyển từ trang chi tiết cơ hội để bắt đầu theo dõi trạng thái." />}
          </section>
        </div>
      ) : null}
    </DashboardLayout>
  );
}

function MetricCard({ icon: Icon, label, value, hint, tone }: { icon: typeof Search; label: string; value: number; hint: string; tone: string }) {
  return <article className={`student-metric student-metric-${tone}`}><span><Icon aria-hidden="true" /></span><div><small>{label}</small><strong>{value}</strong><p>{hint}</p></div></article>;
}

function SectionHeader({ title, description, href, action }: { title: string; description: string; href: string; action: string }) {
  return <header className="student-section-header"><div><h2>{title}</h2><p>{description}</p></div><a href={href}>{action} <ArrowRight aria-hidden="true" /></a></header>;
}

function EmptyState({ title, description }: { title: string; description: string }) {
  return <div className="student-empty"><Sparkles aria-hidden="true" /><h3>{title}</h3><p>{description}</p></div>;
}

export default StudentDashboardPage;
