import { ChevronLeft, ChevronRight, Search } from "lucide-react";
import { useEffect, useState } from "react";
import { EmptyState } from "../../components/common/EmptyState";
import { ErrorState } from "../../components/common/ErrorState";
import { LoadingState } from "../../components/common/LoadingState";
import { ROUTES } from "../../config/routes";
import { applicationApi } from "../../features/applications/api/applicationApi";
import { ApplicationTracker } from "../../features/applications/components/ApplicationTracker";
import type { ApplicationItem } from "../../types/application";
import type { PageResponse } from "../../types/opportunity";

export function ApplicationsPage() {
  const [page, setPage] = useState(0);
  const [result, setResult] = useState<PageResponse<ApplicationItem> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    setLoading(true);
    setError("");
    applicationApi.listMine(page, 12)
      .then(setResult)
      .catch((exception) => {
        setError(exception instanceof Error ? exception.message : "Không thể tải hồ sơ ứng tuyển");
        setResult(null);
      })
      .finally(() => setLoading(false));
  }, [page]);

  const totalPages = result?.totalPages ?? 0;

  return (
    <main className="min-h-screen bg-background text-foreground">
      <section className="mx-auto max-w-6xl px-6 py-10">
        <div className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <a className="text-sm font-semibold text-primary" href={ROUTES.studentDashboard}>Dashboard</a>
            <h1 className="mt-3 text-3xl font-bold">Theo dõi ứng tuyển</h1>
            <p className="mt-2 text-sm text-muted-foreground">Cập nhật trạng thái mới nhất từ tổ chức.</p>
          </div>
          <a className="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2.5 text-sm font-semibold text-primary-foreground" href={ROUTES.explore}>
            <Search className="h-4 w-4" aria-hidden="true" />
            Tìm cơ hội
          </a>
        </div>

        <Pagination
          loading={loading}
          page={page}
          totalPages={totalPages}
          totalElements={result?.totalElements ?? 0}
          onPageChange={setPage}
        />

        {error ? <ErrorState className="mt-6" message={error} /> : null}

        {loading ? (
          <LoadingState className="mt-6" lines={4} />
        ) : null}

        {!loading && !error && result?.content.length ? (
          <div className="mt-6 grid gap-4">
            {result.content.map((application) => (
              <ApplicationTracker key={application.id} application={application} />
            ))}
          </div>
        ) : null}

        {!loading && !error && !result?.content.length ? (
          <EmptyState className="mt-6" title="Bạn chưa ứng tuyển cơ hội nào" description="Ứng tuyển từ trang chi tiết cơ hội để theo dõi trạng thái tại đây." />
        ) : null}
      </section>
    </main>
  );
}

function Pagination({
  loading,
  page,
  totalPages,
  totalElements,
  onPageChange,
}: {
  loading: boolean;
  page: number;
  totalPages: number;
  totalElements: number;
  onPageChange: (page: number) => void;
}) {
  return (
    <div className="mt-6 flex flex-wrap items-center justify-between gap-3">
      <p className="text-sm font-semibold text-muted-foreground">
        {loading ? "Đang tải..." : `${totalElements} hồ sơ ứng tuyển`}
      </p>
      <div className="flex items-center gap-2">
        <button
          className="grid h-10 w-10 place-items-center rounded-md border border-border text-muted-foreground disabled:cursor-not-allowed disabled:opacity-40"
          type="button"
          title="Trang trước"
          aria-label="Trang trước"
          disabled={loading || page <= 0}
          onClick={() => onPageChange(Math.max(page - 1, 0))}
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
          onClick={() => onPageChange(page + 1)}
        >
          <ChevronRight className="h-4 w-4" aria-hidden="true" />
        </button>
      </div>
    </div>
  );
}
