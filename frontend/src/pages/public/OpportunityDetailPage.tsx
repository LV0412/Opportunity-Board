import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { ROUTES } from "../../config/routes";
import { opportunityApi } from "../../features/opportunities/api/opportunityApi";
import { OpportunityDetail } from "../../features/opportunities/components/OpportunityDetail";
import { useAuth } from "../../features/auth/hooks/useAuth";
import type { Opportunity } from "../../types/opportunity";

export function OpportunityDetailPage() {
  const { id = "" } = useParams<{ id: string }>();
  const { user } = useAuth();
  const [opportunity, setOpportunity] = useState<Opportunity | null>(null);
  const [error, setError] = useState("");

  const homeRoute = user?.role === "STUDENT"
    ? ROUTES.studentDashboard
    : user?.role === "ORGANIZATION"
      ? ROUTES.organizationDashboard
      : user?.role === "ADMIN"
        ? ROUTES.adminDashboard
        : ROUTES.home;

  useEffect(() => {
    opportunityApi.getById(id).then(setOpportunity).catch((exception) => {
      setError(exception instanceof Error ? exception.message : "Không tìm thấy cơ hội");
    });
  }, [id]);

  return (
    <main className="min-h-screen bg-background text-foreground">
      <section className="mx-auto max-w-4xl px-6 py-10">
        <a className="text-sm font-semibold text-primary" href={homeRoute}>Trang chủ</a>
        <div className="mt-6">
          {error ? <p className="rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p> : null}
          {opportunity ? <OpportunityDetail opportunity={opportunity} /> : null}
        </div>
      </section>
    </main>
  );
}

export default OpportunityDetailPage;
