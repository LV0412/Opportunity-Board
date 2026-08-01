import { ChevronLeft, ChevronRight, Compass } from "lucide-react";
import { useEffect, useState } from "react";
import { EmptyState } from "../../components/common/EmptyState";
import { ErrorState } from "../../components/common/ErrorState";
import { LoadingState } from "../../components/common/LoadingState";
import { ROUTES } from "../../config/routes";
import { opportunityApi } from "../../features/opportunities/api/opportunityApi";
import { OpportunityCard } from "../../features/opportunities/components/OpportunityCard";
import { OpportunityFilters } from "../../features/opportunities/components/OpportunityFilters";
import type { Opportunity, OpportunitySearchParams, PageResponse } from "../../types/opportunity";

const defaultSearch: OpportunitySearchParams = {
  sort: "newest",
  page: 0,
  size: 12,
};

export function ExplorePage() {
  const [filters, setFilters] = useState<OpportunitySearchParams>(defaultSearch);
  const [result, setResult] = useState<PageResponse<Opportunity> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadOpportunities(filters);
  }, [filters.page]);

  function loadOpportunities(nextFilters: OpportunitySearchParams) {
    setLoading(true);
    setError("");
    opportunityApi.search(nextFilters)
      .then(setResult)
      .catch((exception) => {
        setError(exception instanceof Error ? exception.message : "Không thể tải danh sách cơ hội");
        setResult(null);
      })
      .finally(() => setLoading(false));
  }

  function submitSearch() {
    const nextFilters = { ...filters, page: 0 };
    setFilters(nextFilters);
    loadOpportunities(nextFilters);
  }

  function resetSearch() {
    setFilters(defaultSearch);
    loadOpportunities(defaultSearch);
  }

  const page = result?.number ?? filters.page ?? 0;
  const totalPages = result?.totalPages ?? 0;

  return (
    <main className="min-h-screen bg-background text-foreground">
      <section className="border-b border-border bg-white">
        <div className="mx-auto max-w-6xl px-6 py-8">
          <a className="text-sm font-semibold text-primary" href={ROUTES.home}>Trang chủ</a>
          <div className="mt-5 flex flex-wrap items-end justify-between gap-4">
            <div>
              <p className="inline-flex items-center gap-2 text-sm font-semibold uppercase tracking-wide text-primary">
                <Compass className="h-4 w-4" aria-hidden="true" />
                Explore
              </p>
              <h1 className="mt-3 text-3xl font-bold tracking-normal">Khám phá cơ hội phù hợp</h1>
              <p className="mt-2 max-w-2xl text-sm leading-6 text-muted-foreground">
                Tìm kiếm theo từ khóa, danh mục, địa điểm, kỹ năng, deadline và mức độ phổ biến.
              </p>
            </div>
            <a className="rounded-md border border-border px-4 py-2.5 text-sm font-semibold" href={ROUTES.register}>
              Tạo tài khoản
            </a>
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-6xl px-6 py-8">
        <OpportunityFilters
          value={filters}
          onChange={setFilters}
          onSubmit={submitSearch}
          onReset={resetSearch}
        />

        <div className="mt-6 flex flex-wrap items-center justify-between gap-3">
          <p className="text-sm font-semibold text-muted-foreground">
            {loading ? "Đang tải..." : `${result?.totalElements ?? 0} cơ hội được tìm thấy`}
          </p>
          <div className="flex items-center gap-2">
            <button
              className="grid h-10 w-10 place-items-center rounded-md border border-border text-muted-foreground disabled:cursor-not-allowed disabled:opacity-40"
              type="button"
              title="Trang trước"
              aria-label="Trang trước"
              disabled={loading || page <= 0}
              onClick={() => setFilters((current) => ({ ...current, page: Math.max((current.page ?? 0) - 1, 0) }))}
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
              onClick={() => setFilters((current) => ({ ...current, page: (current.page ?? 0) + 1 }))}
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
            {result.content.map((opportunity) => (
              <OpportunityCard key={opportunity.id} opportunity={opportunity} />
            ))}
          </div>
        ) : null}

        {!loading && !error && !result?.content.length ? (
          <EmptyState className="mt-6" title="Chưa tìm thấy cơ hội phù hợp" description="Thử rút gọn từ khóa hoặc bỏ bớt bộ lọc." />
        ) : null}
      </section>
    </main>
  );
}
