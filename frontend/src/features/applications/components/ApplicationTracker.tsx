import { CheckCircle2, Circle, Clock3, XCircle } from "lucide-react";
import type { ApplicationItem, ApplicationStatus } from "../../../types/application";

const steps: Array<{ status: ApplicationStatus; label: string }> = [
  { status: "APPLIED", label: "Đã nộp" },
  { status: "REVIEWING", label: "Đang xem xét" },
  { status: "ACCEPTED", label: "Được nhận" },
];

const statusLabels: Record<ApplicationStatus, string> = {
  APPLIED: "Đã nộp",
  REVIEWING: "Đang xem xét",
  ACCEPTED: "Được nhận",
  REJECTED: "Từ chối",
};

export function ApplicationTracker({ application }: { application: ApplicationItem }) {
  const currentIndex = steps.findIndex((step) => step.status === application.status);
  const rejected = application.status === "REJECTED";

  return (
    <article className="rounded-md border border-border bg-white p-5 shadow-sm">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <p className="text-sm font-semibold text-primary">{application.opportunityCategoryName}</p>
          <h2 className="mt-2 text-lg font-semibold">{application.opportunityTitle}</h2>
          <p className="mt-1 text-sm text-muted-foreground">{application.organizationName}</p>
        </div>
        <span className={`rounded-md px-3 py-1 text-sm font-semibold ${rejected ? "bg-red-50 text-red-700" : "bg-muted text-foreground"}`}>
          {statusLabels[application.status]}
        </span>
      </div>

      <div className="mt-5 grid gap-3 md:grid-cols-3">
        {steps.map((step, index) => {
          const complete = !rejected && currentIndex >= index;
          const Icon = complete ? CheckCircle2 : Circle;
          return (
            <div key={step.status} className="flex items-center gap-2 text-sm">
              <Icon className={`h-5 w-5 ${complete ? "text-primary" : "text-muted-foreground"}`} aria-hidden="true" />
              <span className={complete ? "font-semibold text-foreground" : "text-muted-foreground"}>{step.label}</span>
            </div>
          );
        })}
      </div>

      {rejected ? (
        <div className="mt-5 inline-flex items-center gap-2 rounded-md bg-red-50 px-3 py-2 text-sm font-semibold text-red-700">
          <XCircle className="h-4 w-4" aria-hidden="true" />
          Hồ sơ chưa phù hợp với cơ hội này
        </div>
      ) : null}

      <div className="mt-5 flex flex-wrap gap-3 text-sm text-muted-foreground">
        <span className="inline-flex items-center gap-1.5">
          <Clock3 className="h-4 w-4" aria-hidden="true" />
          Nộp lúc {new Date(application.appliedAt).toLocaleString("vi-VN")}
        </span>
        {application.resumeFileUrl ? (
          <a className="font-semibold text-primary" href={application.resumeFileUrl} target="_blank" rel="noreferrer">
            Xem CV
          </a>
        ) : null}
      </div>
    </article>
  );
}
