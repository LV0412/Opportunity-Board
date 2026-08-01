import { apiClient } from "../../../config/apiClient";
import type { PageResponse } from "../../../types/opportunity";
import type { UserStatus } from "../../../types/auth";
import type { AdminReport, AdminReportStatus, AdminUser, Category, Tag } from "../../../types/admin";

export const adminApi = {
  listReports(page = 0, size = 20) {
    return apiClient<PageResponse<AdminReport>>(`/admin/reports?page=${page}&size=${size}`);
  },
  updateReportStatus(id: string, status: AdminReportStatus, note = "") {
    return apiClient<AdminReport>(`/admin/reports/${id}/status`, {
      method: "PATCH",
      body: JSON.stringify({ status, note }),
    });
  },
  listUsers(page = 0, size = 20) {
    return apiClient<PageResponse<AdminUser>>(`/admin/users?page=${page}&size=${size}`);
  },
  updateUserStatus(id: string, status: UserStatus) {
    return apiClient<AdminUser>(`/admin/users/${id}/status`, {
      method: "PATCH",
      body: JSON.stringify({ status }),
    });
  },
  listCategories() {
    return apiClient<Category[]>("/admin/categories");
  },
  createCategory(payload: { name: string; slug: string; description?: string }) {
    return apiClient<Category>("/admin/categories", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  updateCategory(id: string, payload: { name: string; slug: string; description?: string }) {
    return apiClient<Category>(`/admin/categories/${id}`, {
      method: "PATCH",
      body: JSON.stringify(payload),
    });
  },
  deleteCategory(id: string) {
    return apiClient<void>(`/admin/categories/${id}`, { method: "DELETE" });
  },
  listTags() {
    return apiClient<Tag[]>("/admin/tags");
  },
  createTag(payload: { name: string; slug: string }) {
    return apiClient<Tag>("/admin/tags", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  updateTag(id: string, payload: { name: string; slug: string }) {
    return apiClient<Tag>(`/admin/tags/${id}`, {
      method: "PATCH",
      body: JSON.stringify(payload),
    });
  },
  deleteTag(id: string) {
    return apiClient<void>(`/admin/tags/${id}`, { method: "DELETE" });
  },
  listPublicCategories() {
    return apiClient<Category[]>("/taxonomy/categories", { skipAuth: true });
  },
  listPublicTags() {
    return apiClient<Tag[]>("/taxonomy/tags", { skipAuth: true });
  },
};
