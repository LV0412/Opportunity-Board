import type { UserRole, UserStatus } from "./auth";

export type AdminReportStatus = "PENDING" | "RESOLVED" | "REJECTED";

export type AdminReport = {
  id: string;
  opportunityId: string;
  opportunityTitle: string;
  reporterId: string;
  reporterName: string;
  reporterEmail: string;
  reason: string;
  description: string | null;
  status: AdminReportStatus;
  createdAt: string;
  updatedAt: string;
};

export type AdminUser = {
  id: string;
  email: string;
  fullName: string;
  role: UserRole;
  status: UserStatus;
  createdAt: string;
};

export type Category = {
  id: string;
  name: string;
  slug: string;
  description: string | null;
};

export type Tag = {
  id: string;
  name: string;
  slug: string;
};
