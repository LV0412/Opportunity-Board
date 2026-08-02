import { ChevronLeft, ChevronRight, FileText } from "lucide-react";
import { useEffect, useState } from "react";
import { EmptyState } from "../../components/common/EmptyState";
import { ErrorState } from "../../components/common/ErrorState";
import { LoadingState } from "../../components/common/LoadingState";
import { ROUTES } from "../../config/routes";
import { applicationApi } from "../../features/applications/api/applicationApi";
import type { ApplicationItem, ApplicationStatus } from "../../types/application";
import type { PageResponse } from "../../types/opportunity";
import { useAuth } from "../../features/auth/hooks/useAuth";
import { DashboardLayout } from "../../layouts/DashboardLayout";

const statuses: Array<{ value: ApplicationStatus; label: string }> = [
  { value: "APPLIED", label: "Đã nộp" },
  { value: "REVIEWING", label: "Đang xem xét" },
  { value: "ACCEPTED", label: "Được nhận" },
  { value: "REJECTED", label: "Từ chối" },
];

export function ApplicantsPage() {
  const { logout } = useAuth();
  const [page, setPage] = useState(0);
  const [result, setResult] = useState<PageResponse<ApplicationItem> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [downloadError, setDownloadError] = useState("");
  const [downloadingId, setDownloadingId] = useState<string | null>(null);

  useEffect(() => {
    loadApplicants(page);
  }, [page]);

  function loadApplicants(nextPage: number) {
    setLoading(true);
    setError("");
    applicationApi.listOrganizationApplications(nextPage, 20)
      .then(setResult)
      .catch((exception) => {
        setError(exception instanceof Error ? exception.message : "Không thể tải ứng viên");
        setResult(null);
      })
      .finally(() => setLoading(false));
  }

  async function updateStatus(applicationId: string, status: ApplicationStatus) {
    const updated = await applicationApi.updateStatus(applicationId, status);
    setResult((current) => {
      if (!current) {
        return current;
      }
      return {
        ...current,
        content: current.content.map((item) => item.id === applicationId ? updated : item),
      };
    });
  }

  async function downloadResume(application: ApplicationItem) {
    if (!application.resumeFileUrl) {
      return;
    }

    setDownloadError("");
    setDownloadingId(application.id);
    try {
      const response = await fetch(application.resumeFileUrl);
      if (!response.ok) {
        throw new Error("Không thể tải CV");
      }

      const objectUrl = URL.createObjectURL(await response.blob());
      const link = document.createElement("a");
      link.href = objectUrl;
      link.download = ensurePdfFileName(application.resumeFileName);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.setTimeout(() => URL.revokeObjectURL(objectUrl), 0);
    } catch (exception) {
      setDownloadError(exception instanceof Error ? exception.message : "Không thể tải CV");
    } finally {
      setDownloadingId(null);
    }
  }

  const totalPages = result?.totalPages ?? 0;

  return (
    <DashboardLayout role="ORGANIZATION" title="Quản lý ứng viên" subtitle="Theo dõi hồ sơ và cập nhật tiến trình tuyển chọn." onLogout={logout}>
      <section className="organization-page-body">
        <div className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <a className="text-sm font-semibold text-primary" href={ROUTES.organizationDashboard}>Dashboard</a>
            <h1 className="mt-3 text-3xl font-bold">Ứng viên</h1>
            <p className="mt-2 text-sm text-muted-foreground">Theo dõi hồ sơ ứng tuyển vào các cơ hội của tổ chức.</p>
          </div>
          <a className="rounded-md border border-border bg-white px-4 py-2.5 text-sm font-semibold" href={ROUTES.organizationOpportunities}>
            Quản lý cơ hội
          </a>
        </div>

        <div className="mt-6 flex flex-wrap items-center justify-between gap-3">
          <p className="text-sm font-semibold text-muted-foreground">
            {loading ? "Đang tải..." : `${result?.totalElements ?? 0} hồ sơ`}
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
        {downloadError ? <ErrorState className="mt-6" message={downloadError} /> : null}

        {loading ? <LoadingState className="mt-6" lines={4} /> : null}

        {!loading && !error && result?.content.length ? (
          <div className="mt-6 overflow-hidden rounded-md border border-border bg-white shadow-sm">
            <div className="hidden grid-cols-[1.2fr_1fr_1fr_160px] gap-4 border-b border-border bg-muted px-4 py-3 text-sm font-semibold text-muted-foreground md:grid">
              <span>Ứng viên</span>
              <span>Cơ hội</span>
              <span>CV</span>
              <span>Trạng thái</span>
            </div>
            {result.content.map((application) => (
              <div key={application.id} className="grid gap-4 border-b border-border px-4 py-4 text-sm last:border-b-0 md:grid-cols-[1.2fr_1fr_1fr_160px]">
                <div>
                  <p className="mb-1 text-xs font-semibold uppercase text-muted-foreground md:hidden">Ứng viên</p>
                  <p className="font-semibold">{application.studentName}</p>
                  <p className="mt-1 text-muted-foreground">{application.studentEmail}</p>
                  <p className="mt-1 text-muted-foreground">{[application.studentUniversity, application.studentMajor].filter(Boolean).join(" - ")}</p>
                </div>
                <div>
                  <p className="mb-1 text-xs font-semibold uppercase text-muted-foreground md:hidden">Cơ hội</p>
                  <p className="font-semibold">{application.opportunityTitle}</p>
                  <p className="mt-1 text-muted-foreground">{new Date(application.appliedAt).toLocaleDateString("vi-VN")}</p>
                </div>
                <div>
                  <p className="mb-1 text-xs font-semibold uppercase text-muted-foreground md:hidden">CV</p>
                  {application.resumeFileUrl ? (
                    <button
                      className="inline-flex items-center gap-2 font-semibold text-primary"
                      type="button"
                      disabled={downloadingId === application.id}
                      onClick={() => void downloadResume(application)}
                    >
                      <FileText className="h-4 w-4" aria-hidden="true" />
                      {downloadingId === application.id ? "Đang tải..." : application.resumeFileName ?? "CV"}
                    </button>
                  ) : (
                    <span className="text-muted-foreground">Chưa có CV</span>
                  )}
                  {application.coverLetter ? <p className="mt-2 line-clamp-2 text-muted-foreground">{application.coverLetter}</p> : null}
                </div>
                <select
                  className="h-10 w-full rounded-md border border-border bg-background px-3 text-sm font-semibold outline-none focus:border-primary"
                  value={application.status}
                  onChange={(event) => updateStatus(application.id, event.target.value as ApplicationStatus)}
                >
                  {statuses.map((status) => (
                    <option key={status.value} value={status.value}>{status.label}</option>
                  ))}
                </select>
              </div>
            ))}
          </div>
        ) : null}

        {!loading && !error && !result?.content.length ? (
          <EmptyState className="mt-6" title="Chưa có ứng viên" description="Khi sinh viên ứng tuyển, hồ sơ sẽ xuất hiện tại đây." />
        ) : null}
      </section>
    </DashboardLayout>
  );
}

function ensurePdfFileName(fileName: string | null) {
  const normalizedName = fileName?.trim() || "cv.pdf";
  return normalizedName.toLowerCase().endsWith(".pdf") ? normalizedName : `${normalizedName}.pdf`;
}

export default ApplicantsPage;
