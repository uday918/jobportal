package com.jobportal.dto;

import com.jobportal.entity.Job;
import com.jobportal.entity.JobStatus;
import java.time.LocalDateTime;

public record JobResponse(Long id, String title, String company, String location, String jobType,
                          String salary, String description, JobStatus status, LocalDateTime createdAt) {
    public static JobResponse from(Job j) {
        return new JobResponse(j.getId(), j.getTitle(), j.getCompany(), j.getLocation(), j.getJobType(),
                j.getSalary(), j.getDescription(), j.getStatus(), j.getCreatedAt());
    }
}
