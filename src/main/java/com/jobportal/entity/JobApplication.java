package com.jobportal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications", indexes = @Index(name = "idx_app_job", columnList = "job_id"))
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long jobId;

    @Column(nullable = false, length = 120)
    private String candidateName;

    @Column(nullable = false, length = 150)
    private String email;

    /** AES-256-GCM encrypted phone number (Base64). */
    @Column(nullable = false, length = 500)
    private String phoneEncrypted;

    @Column(length = 2000)
    private String coverLetter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.NEW;

    @Column(nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @PrePersist
    void onCreate() { appliedAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public Long getJobId() { return jobId; }
    public void setJobId(Long v) { this.jobId = v; }
    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String v) { this.candidateName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPhoneEncrypted() { return phoneEncrypted; }
    public void setPhoneEncrypted(String v) { this.phoneEncrypted = v; }
    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String v) { this.coverLetter = v; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus v) { this.status = v; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
}
