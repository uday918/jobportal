package com.jobportal.dto;

import java.util.List;
import java.util.Map;

public record DashboardResponse(long totalJobs, long openJobs, long totalApplications,
                                long newApplications, List<Map<String, Object>> topJobs) {
}
