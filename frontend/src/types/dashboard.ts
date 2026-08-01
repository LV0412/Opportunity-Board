import type { ApplicationItem } from "./application";
import type { AdminReport } from "./admin";
import type { Opportunity } from "./opportunity";

export type DashboardDeadline = {
  source: "APPLICATION" | "BOOKMARK";
  opportunityId: string;
  opportunityTitle: string;
  organizationName: string;
  deadlineAt: string;
};

export type StudentDashboard = {
  savedCount: number;
  applicationCount: number;
  unreadNotificationCount: number;
  nearestDeadline: DashboardDeadline | null;
  recommendedOpportunities: Opportunity[];
  recentApplications: ApplicationItem[];
};

export type OrganizationOpportunityMetric = {
  opportunityId: string;
  title: string;
  status: Opportunity["status"];
  deadlineAt: string | null;
  viewCount: number;
  bookmarkCount: number;
  applicationCount: number;
  updatedAt: string;
};

export type OrganizationDashboard = {
  totalOpportunities: number;
  pendingOpportunities: number;
  approvedOpportunities: number;
  totalViews: number;
  totalBookmarks: number;
  totalApplications: number;
  recentOpportunities: OrganizationOpportunityMetric[];
};

export type AdminDashboard = {
  pendingOpportunities: number;
  pendingReports: number;
  totalUsers: number;
  totalStudents: number;
  totalOrganizations: number;
  totalOpportunities: number;
  totalApplications: number;
  recentPendingOpportunities: Opportunity[];
  recentReports: AdminReport[];
};
