import { apiClient } from "../../../config/apiClient";
import type { OrganizationProfile, UpdateOrganizationProfileRequest } from "../../../types/profile";

export const organizationApi = {
  getMe() {
    return apiClient<OrganizationProfile>("/organizations/me");
  },
  updateMe(payload: UpdateOrganizationProfileRequest) {
    return apiClient<OrganizationProfile>("/organizations/me", {
      method: "PATCH",
      body: JSON.stringify(payload),
    });
  },
  uploadLogo(file: File) {
    const formData = new FormData();
    formData.append("file", file);
    return apiClient<OrganizationProfile>("/organizations/me/logo", {
      method: "POST",
      body: formData,
    });
  },
};
