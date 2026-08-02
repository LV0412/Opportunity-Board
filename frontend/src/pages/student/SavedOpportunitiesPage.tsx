import { Bookmark, ChevronLeft, ChevronRight, Search } from "lucide-react";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ErrorState } from "../../components/common/ErrorState";
import { LoadingState } from "../../components/common/LoadingState";
import { ROUTES } from "../../config/routes";
import { useAuth } from "../../features/auth/hooks/useAuth";
import { bookmarkApi } from "../../features/bookmarks/api/bookmarkApi";
import { OpportunityCard } from "../../features/opportunities/components/OpportunityCard";
import { DashboardLayout } from "../../layouts/DashboardLayout";
import type { BookmarkItem } from "../../types/bookmark";
import type { PageResponse } from "../../types/opportunity";

export function SavedOpportunitiesPage() {
  const { logout } = useAuth();
  const [page, setPage] = useState(0);
  const [result, setResult] = useState<PageResponse<BookmarkItem> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => { loadSaved(page); }, [page]);
  function loadSaved(nextPage: number) {
    setLoading(true); setError("");
    bookmarkApi.listMine("deadline", nextPage, 12).then(setResult).catch((exception) => { setError(exception instanceof Error ? exception.message : "Không thể tải danh sách đã lưu"); setResult(null); }).finally(() => setLoading(false));
  }
  function removeUnsaved(opportunityId: string) {
    setResult((current) => current ? { ...current, content: current.content.filter((item) => item.opportunity.id !== opportunityId), totalElements: Math.max(current.totalElements - 1, 0) } : current);
  }
  const totalPages = result?.totalPages ?? 0;

  return (
    <DashboardLayout role="STUDENT" title="Cơ hội đã lưu" subtitle="Danh sách ưu tiên của bạn, sắp xếp theo deadline gần nhất." onLogout={logout} actions={<Link className="dashboard-primary-action" to={ROUTES.explore}><Search aria-hidden="true" /> Tìm thêm cơ hội</Link>}>
      <div className="workspace-page">
        <section className="collection-summary"><div className="collection-icon"><Bookmark aria-hidden="true" /></div><div><small>BỘ SƯU TẬP CỦA BẠN</small><strong>{loading ? "—" : String(result?.totalElements ?? 0).padStart(2, "0")}</strong><p>cơ hội đang được lưu để xem lại</p></div><span>Ưu tiên theo deadline</span></section>
        <ListToolbar loading={loading} count={result?.totalElements ?? 0} page={page} totalPages={totalPages} onPageChange={setPage} />
        {error ? <ErrorState className="mt-5" message={error} /> : null}
        {loading ? <LoadingState className="mt-5" lines={6} /> : null}
        {!loading && !error && result?.content.length ? <div className="workspace-card-grid">{result.content.map((item) => <OpportunityCard key={item.id} opportunity={item.opportunity} initiallySaved onBookmarkChange={(id, saved) => { if (!saved) removeUnsaved(id); }} />)}</div> : null}
        {!loading && !error && !result?.content.length ? <div className="workspace-empty"><Bookmark aria-hidden="true" /><h2>Danh sách của bạn đang trống</h2><p>Khám phá và lưu những cơ hội phù hợp để dễ dàng quay lại sau.</p><Link to={ROUTES.explore}>Khám phá cơ hội</Link></div> : null}
      </div>
    </DashboardLayout>
  );
}

function ListToolbar({ loading, count, page, totalPages, onPageChange }: { loading: boolean; count: number; page: number; totalPages: number; onPageChange: (page: number) => void }) {
  return <div className="workspace-toolbar"><p>{loading ? "Đang tải..." : <><strong>{count}</strong> kết quả</>}</p><div><button type="button" aria-label="Trang trước" disabled={loading || page <= 0} onClick={() => onPageChange(Math.max(page - 1, 0))}><ChevronLeft aria-hidden="true" /></button><span>{totalPages ? `${page + 1} / ${totalPages}` : "0 / 0"}</span><button type="button" aria-label="Trang sau" disabled={loading || page + 1 >= totalPages} onClick={() => onPageChange(page + 1)}><ChevronRight aria-hidden="true" /></button></div></div>;
}

export default SavedOpportunitiesPage;
