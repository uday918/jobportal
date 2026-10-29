package com.jobportal.repository;

import com.jobportal.entity.Job;
import com.jobportal.entity.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
    Page<Job> findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCase(String title, String company, Pageable pageable);

    Page<Job> findByStatusAndTitleContainingIgnoreCase(JobStatus status, String title, Pageable pageable);
}
