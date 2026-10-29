package com.jobportal.controller;

import com.jobportal.dto.*;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.DashboardService;
import com.jobportal.service.JobService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Admin panel endpoints. Require a valid JWT with ROLE_ADMIN. */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final JobService jobService;
    private final ApplicationService applicationService;
    private final DashboardService dashboardService;

    public AdminController(JobService jobService, ApplicationService applicationService,
                           DashboardService dashboardService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return dashboardService.stats();
    }

    // ----- Jobs CRUD -----
    @GetMapping("/jobs")
    public PageResponse<JobResponse> jobs(@RequestParam(required = false) String q,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return jobService.adminList(q, Math.max(page, 0), Math.min(Math.max(size, 1), 100));
    }

    @PostMapping("/jobs")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest req) {
        JobResponse created = jobService.create(req);
        return ResponseEntity.created(URI.create("/api/admin/jobs/" + created.id())).body(created);
    }

    @GetMapping("/jobs/{id}")
    public JobResponse getJob(@PathVariable Long id) {
        return jobService.get(id);
    }

    @PutMapping("/jobs/{id}")
    public JobResponse updateJob(@PathVariable Long id, @Valid @RequestBody JobRequest req) {
        return jobService.update(id, req);
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ----- Applications -----
    @GetMapping("/applications")
    public PageResponse<ApplicationView> applications(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        return applicationService.list(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
    }

    @PutMapping("/applications/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest req) {
        applicationService.updateStatus(id, req.status());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/applications/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        applicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
