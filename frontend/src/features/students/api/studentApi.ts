import { apiClient } from "../../../config/apiClient";
import type { Resume, StudentProfile, UpdateStudentProfileRequest } from "../../../types/profile";

export const studentApi = {
  getMe() {
    return apiClient<StudentProfile>("/students/me");
  },
  updateMe(payload: UpdateStudentProfileRequest) {
    return apiClient<StudentProfile>("/students/me", {
      method: "PATCH",
      body: JSON.stringify(payload),
    });
  },
  uploadResume(file: File) {
    const formData = new FormData();
    formData.append("file", file);
    return apiClient<Resume>("/students/me/resume", {
      method: "POST",
      body: formData,
    });
  },
};
