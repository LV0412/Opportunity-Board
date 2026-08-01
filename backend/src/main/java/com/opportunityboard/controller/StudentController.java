package com.opportunityboard.controller;

import com.opportunityboard.dto.request.student.UpdateStudentProfileRequest;
import com.opportunityboard.dto.response.student.ResumeResponse;
import com.opportunityboard.dto.response.student.StudentProfileResponse;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.student.StudentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/students")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/me")
    public StudentProfileResponse me(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return studentService.getMyProfile(currentUser);
    }

    @PatchMapping("/me")
    public StudentProfileResponse update(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody UpdateStudentProfileRequest request
    ) {
        return studentService.updateMyProfile(currentUser, request);
    }

    @PostMapping("/me/resume")
    public ResumeResponse uploadResume(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestPart("file") MultipartFile file
    ) {
        return studentService.uploadResume(currentUser, file);
    }
}
