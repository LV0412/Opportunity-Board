import { Bookmark, CalendarClock, ExternalLink, MapPin } from "lucide-react";
import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { ROUTES } from "../../../config/routes";
import { useAuth } from "../../auth/hooks/useAuth";
import { bookmarkApi } from "../../bookmarks/api/bookmarkApi";
import type { Opportunity } from "../../../types/opportunity";

type Props = {
  opportunity: Opportunity;
  initiallySaved?: boolean;
  onBookmarkChange?: (opportunityId: string, saved: boolean) => void;
};

export function OpportunityCard({ opportunity, initiallySaved = false, onBookmarkChange }: Props) {
  const { user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [saved, setSaved] = useState(initiallySaved);
  const [bookmarkCount, setBookmarkCount] = useState(opportunity.bookmarkCount);
  const [saving, setSaving] = useState(false);
  const deadline = opportunity.deadlineAt
    ? new Date(opportunity.deadlineAt).toLocaleDateString("vi-VN")
    : "Không giới hạn";

  function goToLogin() {
    navigate(ROUTES.login, { state: { from: location.pathname + location.search } });
  }

  async function toggleBookmark() {
    if (!user) {
      goToLogin();
      return;
    }
    if (user.role !== "STUDENT") return;

    setSaving(true);
    try {
      const updated = saved
        ? await bookmarkApi.unsave(opportunity.id)
        : await bookmarkApi.save(opportunity.id);
      const nextSaved = !saved;
      setSaved(nextSaved);
      setBookmarkCount(updated.bookmarkCount);
      onBookmarkChange?.(opportunity.id, nextSaved);
    } finally {
      setSaving(false);
    }
  }

  return (
    <article className="flex h-full flex-col rounded-md border border-outline-variant bg-white p-4 transition-colors hover:border-primary">
      <div className="flex items-start justify-between gap-4">
        <div className="min-w-0">
          <p className="inline-flex rounded bg-primary/5 px-2 py-1 text-[11px] font-semibold uppercase text-primary">{opportunity.categoryName}</p>
          <a className="mt-3 block text-base font-semibold leading-6 text-foreground hover:text-primary" href={`${ROUTES.opportunityDetail}/${opportunity.id}`}>
            {opportunity.title}
          </a>
          <p className="mt-1 text-sm text-muted-foreground">{opportunity.organizationName}</p>
        </div>
        <button
          className={`grid h-9 w-9 shrink-0 place-items-center rounded-md border transition ${
            saved
              ? "border-primary bg-primary text-primary-foreground"
              : "border-border text-muted-foreground hover:border-primary hover:text-primary"
          } disabled:cursor-not-allowed disabled:opacity-60`}
          type="button"
          title={saved ? "Bỏ lưu cơ hội" : "Lưu cơ hội"}
          aria-label={saved ? "Bỏ lưu cơ hội" : "Lưu cơ hội"}
          disabled={saving || Boolean(user && user.role !== "STUDENT")}
          onClick={toggleBookmark}
        >
          <Bookmark className="h-4 w-4" fill={saved ? "currentColor" : "none"} aria-hidden="true" />
        </button>
      </div>

      <p className="mt-3 line-clamp-2 text-sm leading-6 text-muted-foreground">{opportunity.description}</p>

      <div className="mt-4 flex flex-wrap gap-2">
        {opportunity.tags.slice(0, 4).map((tag) => (
          <span key={tag} className="rounded-md bg-muted px-2 py-1 text-xs font-semibold text-foreground">
            {tag}
          </span>
        ))}
      </div>

      <div className="mt-auto border-t border-border pt-4">
        <div className="flex flex-wrap gap-3 text-sm text-muted-foreground">
          <span className="inline-flex items-center gap-1.5">
            <MapPin className="h-4 w-4" aria-hidden="true" />
            {opportunity.remote ? "Từ xa" : opportunity.location ?? "Không rõ"}
          </span>
          <span className="inline-flex items-center gap-1.5">
            <CalendarClock className="h-4 w-4" aria-hidden="true" />
            {deadline}
          </span>
          <span>{bookmarkCount} lượt lưu</span>
        </div>
        <a
          className="mt-4 inline-flex items-center gap-2 text-sm font-semibold text-primary"
          href={`${ROUTES.opportunityDetail}/${opportunity.id}`}
        >
          Xem chi tiết
          <ExternalLink className="h-4 w-4" aria-hidden="true" />
        </a>
      </div>
    </article>
  );
}
