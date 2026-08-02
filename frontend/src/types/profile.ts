export type Resume = {
  id: string;
  fileName: string;
  fileUrl: string;
  primaryResume: boolean;
};

export type VerificationStatus = "UNVERIFIED" | "PENDING" | "VERIFIED" | "REJECTED";

export type StudentProfile = {
  id: string;
  userId: string;
  email: string;
  fullName: string;
  university: string | null;
  major: string | null;
  graduationYear: number | null;
  location: string | null;
  bio: string | null;
  interests: string | null;
  skills: string[];
  resumes: Resume[];
};

export type UpdateStudentProfileRequest = {
  university?: string;
  major?: string;
  graduationYear?: number;
  location?: string;
  bio?: string;
  interests?: string;
  skills?: string[];
};

export type OrganizationProfile = {
  id: string;
  userId: string;
  email: string;
  fullName: string;
  organizationName: string;
  industry: string | null;
  websiteUrl: string | null;
  logoUrl: string | null;
  description: string | null;
  verificationStatus: VerificationStatus;
  verificationNote: string | null;
  verificationRequestedAt: string | null;
  verifiedAt: string | null;
};

export type UpdateOrganizationProfileRequest = {
  organizationName?: string;
  industry?: string;
  websiteUrl?: string;
  description?: string;
};
