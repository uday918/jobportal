package com.jobportal.dto;

import com.jobportal.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull(message = "Status is required") ApplicationStatus status) {
}
