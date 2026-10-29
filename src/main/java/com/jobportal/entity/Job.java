package com.jobportal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 150)
    private String company;

    @Column(length = 100)
    private String location;

    @Column(length = 50)
    private String jobType;

    @Column(length = 100)
    private String salary;

    @Column(length = 4000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JobStatus status = JobStatus.OPEN;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String v) { this.title = v; }
    public String getCompany() { return company; }
    public void setCompany(String v) { this.company = v; }
    public String getLocation() { return location; }
    public void setLocation(String v) { this.location = v; }
    public String getJobType() { return jobType; }
    public void setJobType(String v) { this.jobType = v; }
    public String getSalary() { return salary; }
    public void setSalary(String v) { this.salary = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus v) { this.status = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
