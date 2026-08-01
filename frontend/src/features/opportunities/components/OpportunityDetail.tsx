import { AlertTriangle, Bookmark, CalendarClock, ExternalLink, MapPin } from "lucide-react";
import { useState } from "react";
import { ROUTES } from "../../../config/routes";
import { applicationApi } from "../../applications/api/applicationApi";
import { opportunityApi } from "../api/opportunityApi";
import { useAuth } from "../../auth/hooks/useAuth";
import { bookmarkApi } from "../../bookmarks/api/bookmarkApi";
import type { Opportunity } from "../../../types/opportunity";

export function OpportunityDetail({ opportunity }: { opportunity: Opportunity }) {
  const { user } = useAuth();
  const [saved, setSaved] = useState(false);
  const [bookmarkCount, setBookmarkCount] = useState(opportunity.bookmarkCount);
  const [saving, setSaving] = useState(false);
  const [coverLetter, setCoverLetter] = useState("");
  const [applying, setApplying] = useState(false);
  const [applyMessage, setApplyMessage] = useState("");
  const [applyError, setApplyError] = useState("");
  const [reportReason, setReportReason] = useState("");
  const [reportDescription, setReportDescription] = useState("");
  const [reportMessage, setReportMessage] = useState("");

  function goToLogin() {
    window.history.pushState({}, "", ROUTES.login);
    window.dispatchEvent(new PopStateEvent("popstate"));
  }

  async function toggleBookmark() {
    if (user?.role !== "STUDENT") {
      goToLogin();
      return;
    }

    setSaving(true);
    try {
      const updated = saved
        ? await bookmarkApi.unsave(opportunity.id)
        : await bookmarkApi.save(opportunity.id);
      setSaved(!saved);
      setBookmarkCount(updated.bookmarkCount);
    } finally {
      setSaving(false);
    }
  }

  async function applyOpportunity() {
    if (user?.role !== "STUDENT") {
      goToLogin();
      return;
    }

    setApplying(true);
    setApplyError("");
    setApplyMessage("");
    try {
      await applicationApi.apply(opportunity.id, { coverLetter });
      setApplyMessage("Đã gửi hồ sơ ứng tuyển. Bạn có thể theo dõi trong dashboard.");
      setCoverLetter("");
    } catch (exception) {
      setApplyError(exception instanceof Error ? exception.message : "Không thể ứng tuyển cơ hội này");
    } finally {
      setApplying(false);
    }
  }

  async function submitReport() {
    if (user?.role !== "STUDENT") {
      goToLogin();
      return;
    }
    if (!reportReason.trim()) {
      setApplyError("Vui lòng nhập lý do report.");
      return;
    }

    setApplyError("");
    await fetchReport();
  }

  async function fetchReport() {
    try {
      await opportunityApi.report(opportunity.id, {
        reason: reportReason,
        description: reportDescription,
      });
      setReportMessage("Đã gửi report cho admin kiểm tra.");
      setReportReason("");
      setReportDescription("");
    } catch (exception) {
      setApplyError(exception instanceof Error ? exception.message : "Không thể gửi report");
    }
  }

  return (
    <article className="rounded-md border border-border bg-white p-6 shadow-sm">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <p className="text-sm font-semibold text-primary">{opportunity.categoryName}</p>
          <h1 className="mt-2 text-3xl font-bold">{opportunity.title}</h1>
          <p className="mt-2 text-muted-foreground">{opportunity.organizationName}</p>
        </div>
        <div className="flex items-center gap-2">
          <button
            className={`inline-flex items-center gap-2 rounded-md border px-3 py-2 text-sm font-semibold transition ${
              saved
                ? "border-primary bg-primary text-primary-foreground"
                : "border-border text-foreground hover:border-primary hover:text-primary"
            } disabled:cursor-not-allowed disabled:opacity-60`}
            type="button"
            disabled={saving}
            onClick={toggleBookmark}
          >
            <Bookmark className="h-4 w-4" fill={saved ? "currentColor" : "none"} aria-hidden="true" />
            {saved ? "Đã lưu" : "Lưu"}
          </button>
          <span className="rounded-md bg-muted px-3 py-2 text-sm font-semibold">{opportunity.status}</span>
        </div>
      </div>

      <div className="mt-5 flex flex-wrap gap-3 text-sm text-muted-foreground">
        <span className="inline-flex items-center gap-2"><MapPin className="h-4 w-4" aria-hidden="true" />{opportunity.remote ? "Remote" : opportunity.location ?? "Không rõ địa điểm"}</span>
        {opportunity.deadlineAt ? <span className="inline-flex items-center gap-2"><CalendarClock className="h-4 w-4" aria-hidden="true" />{new Date(opportunity.deadlineAt).toLocaleString()}</span> : null}
        <span>{bookmarkCount} lượt lưu</span>
      </div>

      <div className="mt-5 flex flex-wrap gap-2">
        {opportunity.tags.map((tag) => <span key={tag} className="rounded-md bg-muted px-2 py-1 text-xs font-semibold">{tag}</span>)}
      </div>

      <section className="mt-8">
        <h2 className="text-lg font-semibold">Mô tả</h2>
        <p className="mt-3 whitespace-pre-wrap leading-7 text-muted-foreground">{opportunity.description}</p>
      </section>

      {opportunity.requirements ? (
        <section className="mt-8">
          <h2 className="text-lg font-semibold">Yêu cầu</h2>
          <p className="mt-3 whitespace-pre-wrap leading-7 text-muted-foreground">{opportunity.requirements}</p>
        </section>
      ) : null}

      <section className="mt-8 rounded-md border border-border bg-background p-4">
        <h2 className="text-lg font-semibold">Ứng tuyển</h2>
        <textarea
          className="mt-3 min-h-32 w-full rounded-md border border-border bg-white px-3 py-2 text-sm outline-none focus:border-primary"
          value={coverLetter}
          onChange={(event) => setCoverLetter(event.target.value)}
          placeholder="Viết lời nhắn ngắn cho tổ chức..."
        />
        {applyError ? <p className="mt-3 rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{applyError}</p> : null}
        {applyMessage ? <p className="mt-3 rounded-md bg-emerald-50 px-3 py-2 text-sm text-emerald-700">{applyMessage}</p> : null}
        <div className="mt-4 flex flex-wrap gap-3">
          <button
            className="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2.5 font-semibold text-primary-foreground disabled:cursor-not-allowed disabled:opacity-60"
            type="button"
            disabled={applying}
            onClick={applyOpportunity}
          >
            Ứng tuyển trên hệ thống
          </button>
          {opportunity.applyUrl ? (
            <a className="inline-flex items-center gap-2 rounded-md border border-border bg-white px-4 py-2.5 font-semibold text-foreground" href={opportunity.applyUrl} target="_blank" rel="noreferrer">
              Mở link gốc
              <ExternalLink className="h-4 w-4" aria-hidden="true" />
            </a>
          ) : null}
        </div>
      </section>

      <section className="mt-6 rounded-md border border-border bg-background p-4">
        <h2 className="inline-flex items-center gap-2 text-lg font-semibold">
          <AlertTriangle className="h-5 w-5 text-primary" aria-hidden="true" />
          Báo cáo cơ hội
        </h2>
        <input
          className="mt-3 h-11 w-full rounded-md border border-border bg-white px-3 text-sm outline-none focus:border-primary"
          value={reportReason}
          onChange={(event) => setReportReason(event.target.value)}
          placeholder="Lý do report"
        />
        <textarea
          className="mt-3 min-h-24 w-full rounded-md border border-border bg-white px-3 py-2 text-sm outline-none focus:border-primary"
          value={reportDescription}
          onChange={(event) => setReportDescription(event.target.value)}
          placeholder="Mô tả thêm cho admin"
        />
        {reportMessage ? <p className="mt-3 rounded-md bg-emerald-50 px-3 py-2 text-sm text-emerald-700">{reportMessage}</p> : null}
        <button className="mt-4 rounded-md border border-border bg-white px-4 py-2.5 text-sm font-semibold" type="button" onClick={() => void submitReport()}>
          Gửi report
        </button>
      </section>
    </article>
  );
}
