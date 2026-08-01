import { ChevronLeft, ChevronRight, Search } from "lucide-react";
import { useEffect, useState } from "react";
import { EmptyState } from "../../components/common/EmptyState";
import { ErrorState } from "../../components/common/ErrorState";
import { LoadingState } from "../../components/common/LoadingState";
import { ROUTES } from "../../config/routes";
import { bookmarkApi } from "../../features/bookmarks/api/bookmarkApi";
import { OpportunityCard } from "../../features/opportunities/components/OpportunityCard";
import type { BookmarkItem } from "../../types/bookmark";
import type { PageResponse } from "../../types/opportunity";

export function SavedOpportunitiesPage() {
  const [page, setPage] = useState(0);
  const [result, setResult] = useState<PageResponse<BookmarkItem> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadSaved(page);
  }, [page]);

  function loadSaved(nextPage: number) {
    setLoading(true);
    setError("");
    bookmarkApi.listMine("deadline", nextPage, 12)
      .then(setResult)
      .catch((exception) => {
        setError(exception instanceof Error ? exception.message : "Không thể tải danh sách đã lưu");
        setResult(null);
      })
      .finally(() => setLoading(false));
  }

  function removeUnsaved(opportunityId: string) {
    setResult((current) => {
      if (!current) {
        return current;
      }
      return {
        ...current,
        content: current.content.filter((item) => item.opportunity.id !== opportunityId),
        totalElements: Math.max(current.totalElements - 1, 0),
      };
    });
  }

  const totalPages = result?.totalPages ?? 0;

  return (
    <main className="min-h-screen bg-background text-foreground">
      <section className="mx-auto max-w-6xl px-6 py-10">
        <div className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <a className="text-sm font-semibold text-primary" href={ROUTES.studentDashboard}>Dashboard</a>
            <h1 className="mt-3 text-3xl font-bold">Cơ hội đã lưu</h1>
            <p className="mt-2 text-sm text-muted-foreground">Danh sách được sắp xếp theo deadline gần nhất.</p>
          </div>
          <a className="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2.5 text-sm font-semibold text-primary-foreground" href={ROUTES.explore}>
            <Search className="h-4 w-4" aria-hidden="true" />
            Tìm thêm
          </a>
        </div>

        <div className="mt-6 flex flex-wrap items-center justify-between gap-3">
          <p className="text-sm font-semibold text-muted-foreground">
            {loading ? "Đang tải..." : `${result?.totalElements ?? 0} cơ hội đã lưu`}
          </p>
          <div className="flex items-center gap-2">
            <button
              className="grid h-10 w-10 place-items-center rounded-md border border-border text-muted-foreground disabled:cursor-not-allowed disabled:opacity-40"
              type="button"
              title="Trang trước"
              aria-label="Trang trước"
              disabled={loading || page <= 0}
              onClick={() => setPage((current) => Math.max(current - 1, 0))}
            >
              <ChevronLeft className="h-4 w-4" aria-hidden="true" />
            </button>
            <span className="min-w-20 text-center text-sm font-semibold">
              {totalPages ? `${page + 1}/${totalPages}` : "0/0"}
            </span>
            <button
              className="grid h-10 w-10 place-items-center rounded-md border border-border text-muted-foreground disabled:cursor-not-allowed disabled:opacity-40"
              type="button"
              title="Trang sau"
              aria-label="Trang sau"
              disabled={loading || page + 1 >= totalPages}
              onClick={() => setPage((current) => current + 1)}
            >
              <ChevronRight className="h-4 w-4" aria-hidden="true" />
            </button>
          </div>
        </div>

        {error ? <ErrorState className="mt-6" message={error} /> : null}

        {loading ? (
          <LoadingState className="mt-6" lines={6} />
        ) : null}

        {!loading && !error && result?.content.length ? (
          <div className="mt-6 grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {result.content.map((item) => (
              <OpportunityCard
                key={item.id}
                opportunity={item.opportunity}
                initiallySaved
                onBookmarkChange={(opportunityId, saved) => {
                  if (!saved) {
                    removeUnsaved(opportunityId);
                  }
                }}
              />
            ))}
          </div>
        ) : null}

        {!loading && !error && !result?.content.length ? (
          <EmptyState className="mt-6" title="Bạn chưa lưu cơ hội nào" description="Khám phá và bấm biểu tượng lưu để quay lại sau." />
        ) : null}
      </section>
    </main>
  );
}

export default SavedOpportunitiesPage;
