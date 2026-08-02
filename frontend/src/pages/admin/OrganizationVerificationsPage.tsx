import { useEffect, useState } from "react";
import { CheckCircle2, ExternalLink, ShieldCheck, XCircle } from "lucide-react";
import { ErrorState } from "../../components/common/ErrorState";
import { LoadingState } from "../../components/common/LoadingState";
import { Button } from "../../components/ui/Button";
import { ROUTES } from "../../config/routes";
import { adminApi } from "../../features/admin/api/adminApi";
import type { OrganizationVerification } from "../../types/admin";

export function OrganizationVerificationsPage() {
  const [items, setItems] = useState<OrganizationVerification[]>([]);
  const [loading, setLoading] = useState(true);
  const [workingId, setWorkingId] = useState<string | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    adminApi.listOrganizationVerifications()
      .then((response) => setItems(response.content))
      .catch((exception) => setError(exception instanceof Error ? exception.message : "Không thể tải yêu cầu xác minh"))
      .finally(() => setLoading(false));
  }, []);

  async function approve(item: OrganizationVerification) {
    setWorkingId(item.organizationId);
    setError("");
    try {
      await adminApi.approveOrganizationVerification(item.organizationId);
      setItems((current) => current.filter((candidate) => candidate.organizationId !== item.organizationId));
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Không thể xác minh tổ chức");
    } finally {
      setWorkingId(null);
    }
  }

  async function reject(item: OrganizationVerification) {
    const reason = window.prompt("Nhập lý do từ chối xác minh:");
    if (!reason?.trim()) return;
    setWorkingId(item.organizationId);
    setError("");
    try {
      await adminApi.rejectOrganizationVerification(item.organizationId, reason.trim());
      setItems((current) => current.filter((candidate) => candidate.organizationId !== item.organizationId));
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Không thể từ chối yêu cầu");
    } finally {
      setWorkingId(null);
    }
  }

  return (
    <main className="min-h-screen bg-background text-foreground">
      <section className="mx-auto max-w-6xl px-6 py-10">
        <a className="text-sm font-semibold text-primary" href={ROUTES.adminDashboard}>Về dashboard</a>
        <div className="mt-4 flex items-center gap-3">
          <ShieldCheck className="h-8 w-8 text-primary" aria-hidden="true" />
          <div>
            <h1 className="text-3xl font-bold">Xác minh tổ chức</h1>
            <p className="mt-1 text-muted-foreground">Kiểm tra hồ sơ và website trước khi cấp badge xác minh.</p>
          </div>
        </div>

        {error ? <ErrorState className="mt-6" message={error} /> : null}
        {loading ? <LoadingState className="mt-8" lines={3} /> : null}

        {!loading ? (
          <div className="mt-8 space-y-4">
            {items.map((item) => (
              <article key={item.organizationId} className="rounded-md border border-border bg-white p-5 shadow-sm">
                <div className="flex flex-col justify-between gap-5 md:flex-row md:items-start">
                  <div className="flex min-w-0 gap-4">
                    {item.logoUrl ? (
                      <img className="h-16 w-16 shrink-0 rounded-md border border-border object-cover" src={item.logoUrl} alt="" />
                    ) : null}
                    <div className="min-w-0">
                      <h2 className="text-lg font-semibold">{item.organizationName}</h2>
                      <p className="mt-1 text-sm text-muted-foreground">{item.email}</p>
                      <p className="mt-1 text-sm text-muted-foreground">{item.industry ?? "Chưa có lĩnh vực"}</p>
                      {item.websiteUrl ? (
                        <a className="mt-2 inline-flex items-center gap-1 text-sm font-semibold text-primary" href={item.websiteUrl} target="_blank" rel="noreferrer">
                          Kiểm tra website <ExternalLink className="h-4 w-4" aria-hidden="true" />
                        </a>
                      ) : null}
                    </div>
                  </div>
                  <div className="flex shrink-0 flex-wrap gap-2">
                    <Button disabled={workingId === item.organizationId} icon={<CheckCircle2 className="h-4 w-4" />} onClick={() => void approve(item)}>
                      Xác minh
                    </Button>
                    <Button variant="danger" disabled={workingId === item.organizationId} icon={<XCircle className="h-4 w-4" />} onClick={() => void reject(item)}>
                      Từ chối
                    </Button>
                  </div>
                </div>
                {item.description ? <p className="mt-4 border-t border-border pt-4 text-sm leading-6 text-muted-foreground">{item.description}</p> : null}
                {item.verificationRequestedAt ? <p className="mt-3 text-xs text-muted-foreground">Gửi lúc {new Date(item.verificationRequestedAt).toLocaleString("vi-VN")}</p> : null}
              </article>
            ))}
            {!items.length ? <p className="rounded-md border border-border bg-white p-5 text-sm text-muted-foreground">Không có yêu cầu xác minh đang chờ.</p> : null}
          </div>
        ) : null}
      </section>
    </main>
  );
}

export default OrganizationVerificationsPage;
