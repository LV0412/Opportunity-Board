import { apiClient } from "../../../config/apiClient";
import type { BookmarkItem } from "../../../types/bookmark";
import type { Opportunity, PageResponse } from "../../../types/opportunity";

export const bookmarkApi = {
  save(opportunityId: string) {
    return apiClient<Opportunity>(`/opportunities/${opportunityId}/bookmark`, {
      method: "POST",
    });
  },
  unsave(opportunityId: string) {
    return apiClient<Opportunity>(`/opportunities/${opportunityId}/bookmark`, {
      method: "DELETE",
    });
  },
  listMine(sort = "deadline", page = 0, size = 12) {
    const params = new URLSearchParams({
      sort,
      page: String(page),
      size: String(size),
    });
    return apiClient<PageResponse<BookmarkItem>>(`/bookmarks/me?${params.toString()}`);
  },
};
