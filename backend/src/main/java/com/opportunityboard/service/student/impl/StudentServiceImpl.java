package com.opportunityboard.service.student.impl;

import com.opportunityboard.dto.request.student.UpdateStudentProfileRequest;
import com.opportunityboard.dto.response.student.ResumeResponse;
import com.opportunityboard.dto.response.student.StudentProfileResponse;
import com.opportunityboard.entity.Resume;
import com.opportunityboard.entity.Skill;
import com.opportunityboard.entity.StudentProfile;
import com.opportunityboard.infrastructure.storage.StorageService;
import com.opportunityboard.repository.ResumeRepository;
import com.opportunityboard.repository.SkillRepository;
import com.opportunityboard.repository.StudentProfileRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.student.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class StudentServiceImpl implements StudentService {
    private static final long MAX_RESUME_SIZE_BYTES = 5 * 1024 * 1024;

    private final StudentProfileRepository studentProfileRepository;
    private final SkillRepository skillRepository;
    private final ResumeRepository resumeRepository;
    private final StorageService storageService;

    public StudentServiceImpl(
            StudentProfileRepository studentProfileRepository,
            SkillRepository skillRepository,
            ResumeRepository resumeRepository,
            StorageService storageService
    ) {
        this.studentProfileRepository = studentProfileRepository;
        this.skillRepository = skillRepository;
        this.resumeRepository = resumeRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getMyProfile(CustomUserDetails currentUser) {
        return toResponse(findMyProfile(currentUser));
    }

    @Override
    @Transactional
    public StudentProfileResponse updateMyProfile(CustomUserDetails currentUser, UpdateStudentProfileRequest request) {
        StudentProfile profile = findMyProfile(currentUser);
        profile.setUniversity(trimToNull(request.university()));
        profile.setMajor(trimToNull(request.major()));
        profile.setGraduationYear(request.graduationYear());
        profile.setLocation(trimToNull(request.location()));
        profile.setBio(trimToNull(request.bio()));
        profile.setInterests(trimToNull(request.interests()));

        if (request.skills() != null) {
            profile.setSkills(resolveSkills(request.skills()));
        }

        return toResponse(studentProfileRepository.save(profile));
    }

    @Override
    @Transactional
    public ResumeResponse uploadResume(CustomUserDetails currentUser, MultipartFile file) {
        validateResume(file);
        StudentProfile profile = findMyProfile(currentUser);
        String fileUrl = storageService.uploadResume(file);

        Resume resume = new Resume();
        resume.setStudent(profile);
        resume.setFileName(file.getOriginalFilename() == null ? "resume.pdf" : file.getOriginalFilename());
        resume.setFileUrl(fileUrl);
        resume.setPrimaryResume(resumeRepository.findByStudentId(profile.getId()).isEmpty());

        return toResumeResponse(resumeRepository.save(resume));
    }

    private StudentProfile findMyProfile(CustomUserDetails currentUser) {
        return studentProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student profile not found"));
    }

    private Set<Skill> resolveSkills(List<String> skillNames) {
        Set<Skill> skills = new HashSet<>();
        for (String rawName : skillNames) {
            String name = trimToNull(rawName);
            if (name == null) {
                continue;
            }
            String slug = slugify(name);
            Skill skill = skillRepository.findBySlug(slug).orElseGet(() -> {
                Skill created = new Skill();
                created.setName(name);
                created.setSlug(slug);
                return skillRepository.save(created);
            });
            skills.add(skill);
        }
        return skills;
    }

    private void validateResume(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng chọn tệp CV");
        }
        if (file.getSize() > MAX_RESUME_SIZE_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Tệp CV phải có dung lượng không quá 5 MB");
        }
        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tệp CV phải có định dạng PDF");
        }
    }

    private StudentProfileResponse toResponse(StudentProfile profile) {
        List<String> skills = profile.getSkills().stream()
                .map(Skill::getName)
                .sorted()
                .toList();
        List<ResumeResponse> resumes = profile.getResumes().stream()
                .sorted(Comparator.comparing(Resume::getCreatedAt).reversed())
                .map(this::toResumeResponse)
                .toList();

        return new StudentProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getUser().getEmail(),
                profile.getUser().getFullName(),
                profile.getUniversity(),
                profile.getMajor(),
                profile.getGraduationYear(),
                profile.getLocation(),
                profile.getBio(),
                profile.getInterests(),
                skills,
                resumes
        );
    }

    private ResumeResponse toResumeResponse(Resume resume) {
        return new ResumeResponse(
                resume.getId(),
                resume.getFileName(),
                resume.getFileUrl(),
                resume.isPrimaryResume()
        );
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String slugify(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized.isBlank() ? value.toLowerCase(Locale.ROOT).replaceAll("\\s+", "-") : normalized;
    }
}
