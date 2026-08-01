export type OpportunityStatus = "DRAFT" | "PENDING" | "APPROVED" | "REJECTED" | "CLOSED";

export type Opportunity = {
  id: string;
  title: string;
  description: string;
  requirements: string | null;
  location: string | null;
  remote: boolean;
  applyUrl: string | null;
  deadlineAt: string | null;
  status: OpportunityStatus;
  categoryName: string;
  categorySlug: string;
  tags: string[];
  organizationId: string;
  organizationName: string;
  organizationLogoUrl: string | null;
  viewCount: number;
  bookmarkCount: number;
  latestReviewNote: string | null;
  createdAt: string;
  updatedAt: string;
};

export type OpportunityPayload = {
  title: string;
  description: string;
  requirements?: string;
  location?: string;
  remote: boolean;
  applyUrl?: string;
  deadlineAt?: string;
  categorySlug: string;
  tags: string[];
};

export type OpportunitySort = "newest" | "deadline" | "popular";

export type OpportunitySearchParams = {
  query?: string;
  categorySlug?: string;
  location?: string;
  deadlineBefore?: string;
  field?: string;
  skill?: string;
  remote?: boolean;
  sort?: OpportunitySort;
  page?: number;
  size?: number;
};

export type PageResponse<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
};
