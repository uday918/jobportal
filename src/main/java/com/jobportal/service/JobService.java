package com.jobportal.service;

import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.dto.PageResponse;
import com.jobportal.entity.Job;
import com.jobportal.entity.JobStatus;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobService {

    private final JobRepository jobs;
    private final ApplicationRepository applications;

    public JobService(JobRepository jobs, ApplicationRepository applications) {
        this.jobs = jobs;
        this.applications = applications;
    }

    // ---------- admin ----------
    @Transactional(readOnly = true)
    public PageResponse<JobResponse> adminList(String q, int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Job> result = (q == null || q.isBlank())
                ? jobs.findAll(pr)
                : jobs.findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCase(q, q, pr);
        return PageResponse.from(result.map(JobResponse::from));
    }

    public JobResponse create(JobRequest req) {
        Job job = new Job();
        apply(job, req);
        return JobResponse.from(jobs.save(job));
    }

    public JobResponse update(Long id, JobRequest req) {
        Job job = find(id);
        apply(job, req);
        return JobResponse.from(job);
    }

    public void delete(Long id) {
        Job job = find(id);
        applications.deleteByJobId(id);
        jobs.delete(job);
    }

    // ---------- public ----------
    @Transactional(readOnly = true)
    public PageResponse<JobResponse> publicList(String q, int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Job> result = jobs.findByStatusAndTitleContainingIgnoreCase(JobStatus.OPEN, q == null ? "" : q, pr);
        return PageResponse.from(result.map(JobResponse::from));
    }

    @Transactional(readOnly = true)
    public JobResponse get(Long id) {
        return JobResponse.from(find(id));
    }

    public Job find(Long id) {
        return jobs.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job not found with id " + id));
    }

    private void apply(Job job, JobRequest req) {
        job.setTitle(req.title().trim());
        job.setCompany(req.company().trim());
        job.setLocation(req.location());
        job.setJobType(req.jobType());
        job.setSalary(req.salary());
        job.setDescription(req.description());
        job.setStatus(req.status() == null ? JobStatus.OPEN : req.status());
    }
}
