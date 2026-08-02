import { ArrowRight, ChevronLeft, ChevronRight, ClipboardCheck, Search } from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { ErrorState } from "../../components/common/ErrorState";
import { LoadingState } from "../../components/common/LoadingState";
import { ROUTES } from "../../config/routes";
import { applicationApi } from "../../features/applications/api/applicationApi";
import { ApplicationTracker } from "../../features/applications/components/ApplicationTracker";
import { useAuth } from "../../features/auth/hooks/useAuth";
import { DashboardLayout } from "../../layouts/DashboardLayout";
import type { ApplicationItem } from "../../types/application";
import type { PageResponse } from "../../types/opportunity";

export function ApplicationsPage() {
  const { logout } = useAuth();
  const [page, setPage] = useState(0);
  const [result, setResult] = useState<PageResponse<ApplicationItem> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => { setLoading(true); setError(""); applicationApi.listMine(page, 12).then(setResult).catch((exception) => { setError(exception instanceof Error ? exception.message : "Không thể tải hồ sơ ứng tuyển"); setResult(null); }).finally(() => setLoading(false)); }, [page]);
  const counts = useMemo(() => ({ reviewing: result?.content.filter((item) => item.status === "REVIEWING").length ?? 0, accepted: result?.content.filter((item) => item.status === "ACCEPTED").length ?? 0 }), [result]);
  const totalPages = result?.totalPages ?? 0;

  return (
    <DashboardLayout role="STUDENT" title="Theo dõi ứng tuyển" subtitle="Một nơi để theo dõi toàn bộ hành trình ứng tuyển của bạn." onLogout={logout} actions={<Link className="dashboard-primary-action" to={ROUTES.explore}><Search aria-hidden="true" /> Tìm cơ hội mới</Link>}>
      <div className="workspace-page">
        <section className="application-overview">
          <div><small>TỔNG HỒ SƠ</small><strong>{String(result?.totalElements ?? 0).padStart(2, "0")}</strong><p>cơ hội đã ứng tuyển</p></div>
          <div><small>ĐANG XEM XÉT</small><strong>{String(counts.reviewing).padStart(2, "0")}</strong><p>trên trang hiện tại</p></div>
          <div><small>ĐƯỢC CHẤP NHẬN</small><strong>{String(counts.accepted).padStart(2, "0")}</strong><p>trên trang hiện tại</p></div>
        </section>
        <div className="workspace-toolbar"><p>{loading ? "Đang tải..." : <><strong>{result?.totalElements ?? 0}</strong> hồ sơ ứng tuyển</>}</p><div><button type="button" aria-label="Trang trước" disabled={loading || page <= 0} onClick={() => setPage(Math.max(page - 1, 0))}><ChevronLeft aria-hidden="true" /></button><span>{totalPages ? `${page + 1} / ${totalPages}` : "0 / 0"}</span><button type="button" aria-label="Trang sau" disabled={loading || page + 1 >= totalPages} onClick={() => setPage(page + 1)}><ChevronRight aria-hidden="true" /></button></div></div>
        {error ? <ErrorState className="mt-5" message={error} /> : null}
        {loading ? <LoadingState className="mt-5" lines={4} /> : null}
        {!loading && !error && result?.content.length ? <div className="tracker-list">{result.content.map((application) => <ApplicationTracker key={application.id} application={application} />)}</div> : null}
        {!loading && !error && !result?.content.length ? <div className="workspace-empty"><ClipboardCheck aria-hidden="true" /><h2>Chưa có hồ sơ ứng tuyển</h2><p>Khám phá cơ hội phù hợp và bắt đầu hành trình đầu tiên của bạn.</p><Link to={ROUTES.explore}>Tìm cơ hội <ArrowRight aria-hidden="true" /></Link></div> : null}
      </div>
    </DashboardLayout>
  );
}

export default ApplicationsPage;
