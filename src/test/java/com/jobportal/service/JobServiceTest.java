package com.jobportal.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.dto.PageResponse;
import com.jobportal.entity.Job;
import com.jobportal.entity.JobStatus;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobs;

    @Mock
    private ApplicationRepository applications;

    @InjectMocks
    private JobService service;

    @Test
    void adminList_whenQueryBlank_shouldReturnMappedPage() {
        Job job = newJob(10L, "Java Developer", "Acme", JobStatus.OPEN);
        Page<Job> page = new PageImpl<>(List.of(job), PageRequest.of(0, 10), 1);
        when(jobs.findAll(any(Pageable.class))).thenReturn(page);

        PageResponse<JobResponse> result = service.adminList("   ", 0, 10);

        assertEquals(1, result.content().size());
        assertEquals(1, result.totalElements());
        assertEquals("Java Developer", result.content().get(0).title());
        assertEquals(0, result.page());
    }

    @Test
    void adminList_whenQueryProvided_shouldUseSearchRepository() {
        Job job = newJob(11L, "Java Backend", "CloudX", JobStatus.OPEN);
        Page<Job> page = new PageImpl<>(List.of(job), PageRequest.of(0, 10), 1);
        when(jobs.findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCase(eq("java"), eq("java"), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<JobResponse> result = service.adminList("java", 0, 10);

        assertEquals(1, result.content().size());
        assertEquals("Java Backend", result.content().get(0).title());
        verify(jobs).findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCase(eq("java"), eq("java"), any(Pageable.class));
    }

    @Test
    void create_shouldSaveMappedJobAndReturnResponse() {
        JobRequest request = new JobRequest(
                "  Java Developer  ",
                "  Acme Corp  ",
                "Pune",
                "Full-time",
                "18 LPA",
                "Build APIs",
                JobStatus.OPEN
        );
        Job saved = newJob(22L, "Java Developer", "Acme Corp", JobStatus.OPEN);
        saved.setLocation("Pune");
        saved.setJobType("Full-time");
        saved.setSalary("18 LPA");
        saved.setDescription("Build APIs");
        when(jobs.save(any(Job.class))).thenReturn(saved);

        JobResponse result = service.create(request);

        assertEquals("Java Developer", result.title());
        assertEquals("Acme Corp", result.company());
        assertEquals(JobStatus.OPEN, result.status());
        verify(jobs).save(any(Job.class));
    }

    @Test
    void update_shouldLoadExistingJobAndApplyChanges() {
        Job existing = newJob(5L, "Old Title", "Old Company", JobStatus.OPEN);
        when(jobs.findById(5L)).thenReturn(Optional.of(existing));
        JobRequest request = new JobRequest(
                "Senior Java Developer",
                "CloudNest",
                "Remote",
                "Contract",
                "80/h",
                "Lead backend services",
                JobStatus.CLOSED
        );

        JobResponse result = service.update(5L, request);

        assertEquals("Senior Java Developer", result.title());
        assertEquals("CloudNest", result.company());
        assertEquals(JobStatus.CLOSED, result.status());
    }

    @Test
    void delete_shouldDeleteJobApplicationsAndJob() {
        Job existing = newJob(7L, "QA Engineer", "Example", JobStatus.OPEN);
        when(jobs.findById(7L)).thenReturn(Optional.of(existing));

        service.delete(7L);

        verify(applications).deleteByJobId(7L);
        verify(jobs).delete(existing);
    }

    @Test
    void publicList_shouldReturnOnlyOpenJobsMatchingQuery() {
        Job job = newJob(8L, "Java Engineer", "BootLabs", JobStatus.OPEN);
        Page<Job> page = new PageImpl<>(List.of(job), PageRequest.of(0, 10), 1);
        when(jobs.findByStatusAndTitleContainingIgnoreCase(eq(JobStatus.OPEN), eq("java"), any(Pageable.class))).thenReturn(page);

        PageResponse<JobResponse> result = service.publicList("java", 0, 10);

        assertEquals(1, result.content().size());
        assertEquals(JobStatus.OPEN, result.content().get(0).status());
        verify(jobs).findByStatusAndTitleContainingIgnoreCase(eq(JobStatus.OPEN), eq("java"), any(Pageable.class));
    }

    @Test
    void get_shouldReturnMappedJob() {
        Job job = newJob(9L, "Backend Engineer", "FinServe", JobStatus.OPEN);
        when(jobs.findById(9L)).thenReturn(Optional.of(job));

        JobResponse result = service.get(9L);

        assertEquals(9L, result.id());
        assertEquals("Backend Engineer", result.title());
    }

    @Test
    void find_shouldThrowWhenJobNotFound() {
        when(jobs.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.find(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void get_shouldThrowWhenJobNotFound() {
        when(jobs.findById(42L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.get(42L));
    }

    private Job newJob(Long id, String title, String company, JobStatus status) {
        Job job = new Job();
        setId(job, id);
        job.setTitle(title);
        job.setCompany(company);
        job.setLocation("Mumbai");
        job.setJobType("Full-time");
        job.setSalary("20 LPA");
        job.setDescription("Work with Java");
        job.setStatus(status);
        return job;
    }

    private void setId(Job job, Long id) {
        try {
            java.lang.reflect.Field field = Job.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(job, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to set job id in test", e);
        }
    }
}
