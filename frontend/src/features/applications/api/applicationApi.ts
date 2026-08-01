import { apiClient } from "../../../config/apiClient";
import type { ApplicationItem, ApplicationStatus, ApplyPayload } from "../../../types/application";
import type { PageResponse } from "../../../types/opportunity";

export const applicationApi = {
  apply(opportunityId: string, payload: ApplyPayload) {
    return apiClient<ApplicationItem>(`/opportunities/${opportunityId}/apply`, {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  listMine(page = 0, size = 12) {
    return apiClient<PageResponse<ApplicationItem>>(`/applications/me?page=${page}&size=${size}`);
  },
  getById(id: string) {
    return apiClient<ApplicationItem>(`/applications/${id}`);
  },
  updateStatus(id: string, status: ApplicationStatus) {
    return apiClient<ApplicationItem>(`/applications/${id}/status`, {
      method: "PATCH",
      body: JSON.stringify({ status }),
    });
  },
  listOrganizationApplications(page = 0, size = 20) {
    return apiClient<PageResponse<ApplicationItem>>(`/organizations/me/applications?page=${page}&size=${size}`);
  },
};
