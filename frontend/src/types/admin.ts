import type { UserRole, UserStatus } from "./auth";
import type { VerificationStatus } from "./profile";

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

export type OrganizationVerification = {
  organizationId: string;
  organizationName: string;
  email: string;
  industry: string | null;
  websiteUrl: string | null;
  logoUrl: string | null;
  description: string | null;
  verificationStatus: VerificationStatus;
  verificationNote: string | null;
  verificationRequestedAt: string | null;
  verifiedAt: string | null;
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
