package com.jobportal.service;

import com.jobportal.dto.DashboardResponse;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/** Dashboard statistics using plain SQL through JdbcTemplate. */
@Service
public class DashboardService {

    private final JdbcTemplate jdbc;

    public DashboardService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public DashboardResponse stats() {
        long totalJobs = count("SELECT COUNT(*) FROM jobs");
        long openJobs = count("SELECT COUNT(*) FROM jobs WHERE status = 'OPEN'");
        long totalApps = count("SELECT COUNT(*) FROM applications");
        long newApps = count("SELECT COUNT(*) FROM applications WHERE status = 'NEW'");
        List<Map<String, Object>> top = jdbc.queryForList(
                "SELECT j.title AS title, COUNT(a.id) AS applications "
                + "FROM jobs j LEFT JOIN applications a ON a.job_id = j.id "
                + "GROUP BY j.id, j.title ORDER BY applications DESC, j.id DESC LIMIT 5");
        return new DashboardResponse(totalJobs, openJobs, totalApps, newApps, top);
    }

    private long count(String sql) {
        Long v = jdbc.queryForObject(sql, Long.class);
        return v == null ? 0 : v;
    }
}
