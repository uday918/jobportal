package com.jobportal.dto;

import com.jobportal.entity.JobStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JobRequest(
        @NotBlank(message = "Title is required") @Size(max = 150) String title,
        @NotBlank(message = "Company is required") @Size(max = 150) String company,
        @Size(max = 100) String location,
        @Size(max = 50) String jobType,
        @Size(max = 100) String salary,
        @Size(max = 4000) String description,
        JobStatus status) {
}
