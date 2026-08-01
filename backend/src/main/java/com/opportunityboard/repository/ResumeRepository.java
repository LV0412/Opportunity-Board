package com.opportunityboard.repository;

import com.opportunityboard.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {
    List<Resume> findByStudentId(UUID studentId);

    Optional<Resume> findByStudentIdAndPrimaryResumeTrue(UUID studentId);
}
