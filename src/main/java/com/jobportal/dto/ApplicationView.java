package com.jobportal.dto;

import java.time.LocalDateTime;

public record ApplicationView(Long id, Long jobId, String jobTitle, String candidateName, String email,
                              String phone, String coverLetter, String status, LocalDateTime appliedAt) {
}
