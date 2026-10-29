package com.jobportal.controller;

import com.jobportal.dto.ApplyRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.dto.PageResponse;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Public (no login) endpoints used by the job seeker frontend. */
@RestController
@RequestMapping("/api/public/jobs")
public class PublicController {

    private final JobService jobService;
    private final ApplicationService applicationService;

    public PublicController(JobService jobService, ApplicationService applicationService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    @GetMapping
    public PageResponse<JobResponse> list(@RequestParam(required = false) String q,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "6") int size) {
        return jobService.publicList(q, Math.max(page, 0), Math.min(Math.max(size, 1), 50));
    }

    @GetMapping("/{id}")
    public JobResponse get(@PathVariable Long id) {
        return jobService.get(id);
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<Map<String, String>> apply(@PathVariable Long id, @Valid @RequestBody ApplyRequest req) {
        applicationService.apply(id, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Application submitted successfully"));
    }
}
