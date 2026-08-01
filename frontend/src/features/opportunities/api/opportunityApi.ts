import { apiClient } from "../../../config/apiClient";
import type { Opportunity, OpportunityPayload, OpportunitySearchParams, PageResponse } from "../../../types/opportunity";

function toSearchQuery(params: OpportunitySearchParams) {
  const searchParams = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null || value === "") {
      return;
    }
    searchParams.set(key, String(value));
  });
  return searchParams.toString();
}

export const opportunityApi = {
  listApproved() {
    return apiClient<PageResponse<Opportunity>>("/opportunities?size=20", { skipAuth: true });
  },
  search(params: OpportunitySearchParams) {
    const query = toSearchQuery(params);
    return apiClient<PageResponse<Opportunity>>(`/opportunities/search?${query}`, { skipAuth: true });
  },
  getById(id: string) {
    return apiClient<Opportunity>(`/opportunities/${id}`, { skipAuth: true });
  },
  report(id: string, payload: { reason: string; description?: string }) {
    return apiClient(`/opportunities/${id}/reports`, {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  listMine() {
    return apiClient<PageResponse<Opportunity>>("/opportunities/mine?size=50");
  },
  create(payload: OpportunityPayload) {
    return apiClient<Opportunity>("/opportunities", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  update(id: string, payload: OpportunityPayload) {
    return apiClient<Opportunity>(`/opportunities/${id}`, {
      method: "PATCH",
      body: JSON.stringify(payload),
    });
  },
  close(id: string) {
    return apiClient<void>(`/opportunities/${id}`, {
      method: "DELETE",
    });
  },
  listPending() {
    return apiClient<PageResponse<Opportunity>>("/admin/opportunities/pending?size=50");
  },
  approve(id: string) {
    return apiClient<Opportunity>(`/admin/opportunities/${id}/approve`, {
      method: "POST",
    });
  },
  reject(id: string, reason: string) {
    return apiClient<Opportunity>(`/admin/opportunities/${id}/reject`, {
      method: "POST",
      body: JSON.stringify({ reason }),
    });
  },
};
