package com.opportunityboard.service.student;

import com.opportunityboard.dto.request.student.UpdateStudentProfileRequest;
import com.opportunityboard.dto.response.student.ResumeResponse;
import com.opportunityboard.dto.response.student.StudentProfileResponse;
import com.opportunityboard.security.CustomUserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface StudentService {
    StudentProfileResponse getMyProfile(CustomUserDetails currentUser);

    StudentProfileResponse updateMyProfile(CustomUserDetails currentUser, UpdateStudentProfileRequest request);

    ResumeResponse uploadResume(CustomUserDetails currentUser, MultipartFile file);
}
