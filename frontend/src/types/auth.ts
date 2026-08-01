export type UserRole = "STUDENT" | "ORGANIZATION" | "ADMIN";
export type UserStatus = "ACTIVE" | "LOCKED" | "DISABLED";

export type AuthUser = {
  id: string;
  email: string;
  fullName: string;
  role: UserRole;
  status: UserStatus;
};

export type AuthResponse = {
  accessToken: string;
  tokenType: "Bearer";
  expiresIn: number;
  user: AuthUser;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type RegisterRequest = LoginRequest & {
  fullName: string;
  role: UserRole;
  organizationName?: string;
  university?: string;
  major?: string;
};
