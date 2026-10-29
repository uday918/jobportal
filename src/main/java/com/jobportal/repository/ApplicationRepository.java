package com.jobportal.repository;

import com.jobportal.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<JobApplication, Long> {
    void deleteByJobId(Long jobId);
}
