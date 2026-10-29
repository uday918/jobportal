package com.jobportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ApplyRequest(
        @NotBlank(message = "Name is required") @Size(max = 120) String candidateName,
        @NotBlank(message = "Email is required") @Email(message = "Invalid email") String email,
        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[0-9+\\- ]{7,15}$", message = "Invalid phone number") String phone,
        @Size(max = 2000) String coverLetter) {
}
