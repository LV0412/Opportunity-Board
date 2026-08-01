export type ApplicationStatus = "APPLIED" | "REVIEWING" | "ACCEPTED" | "REJECTED";

export type ApplicationItem = {
  id: string;
  status: ApplicationStatus;
  coverLetter: string | null;
  opportunityId: string;
  opportunityTitle: string;
  opportunityCategoryName: string;
  organizationName: string;
  studentId: string;
  studentName: string;
  studentEmail: string;
  studentUniversity: string | null;
  studentMajor: string | null;
  resumeId: string | null;
  resumeFileName: string | null;
  resumeFileUrl: string | null;
  appliedAt: string;
  updatedAt: string;
};

export type ApplyPayload = {
  resumeId?: string;
  coverLetter?: string;
};
