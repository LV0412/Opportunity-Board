package com.opportunityboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "resumes")
public class Resume extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    @Column(nullable = false, length = 120)
    private String fileName;

    @Column(nullable = false, length = 255)
    private String fileUrl;

    @Column(nullable = false)
    private boolean primaryResume = false;

    @OneToMany(mappedBy = "resume")
    private Set<Application> applications = new HashSet<>();

    public StudentProfile getStudent() {
        return student;
    }

    public void setStudent(StudentProfile student) {
        this.student = student;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public boolean isPrimaryResume() {
        return primaryResume;
    }

    public void setPrimaryResume(boolean primaryResume) {
        this.primaryResume = primaryResume;
    }
}
