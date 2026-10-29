package com.jobportal.service;

import com.jobportal.dto.ApplicationView;
import com.jobportal.dto.ApplyRequest;
import com.jobportal.dto.PageResponse;
import com.jobportal.entity.ApplicationStatus;
import com.jobportal.entity.Job;
import com.jobportal.entity.JobApplication;
import com.jobportal.entity.JobStatus;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.security.AesGcmEncryptor;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ApplicationService {

    private final ApplicationRepository repository;
    private final JobService jobService;
    private final AesGcmEncryptor encryptor;
    private final JdbcTemplate jdbc;

    public ApplicationService(ApplicationRepository repository, JobService jobService,
                              AesGcmEncryptor encryptor, JdbcTemplate jdbc) {
        this.repository = repository;
        this.jobService = jobService;
        this.encryptor = encryptor;
        this.jdbc = jdbc;
    }

    /** Candidate applies to a job (JPA write). Phone number is stored AES-256 encrypted. */
    public void apply(Long jobId, ApplyRequest req) {
        Job job = jobService.find(jobId);
        if (job.getStatus() != JobStatus.OPEN) {
            throw new IllegalArgumentException("This job is closed for applications");
        }
        JobApplication app = new JobApplication();
        app.setJobId(jobId);
        app.setCandidateName(req.candidateName().trim());
        app.setEmail(req.email().trim());
        app.setPhoneEncrypted(encryptor.encrypt(req.phone().trim()));
        app.setCoverLetter(req.coverLetter());
        repository.save(app);
    }

    /** Admin listing (JdbcTemplate read with JOIN + pagination). */
    @Transactional(readOnly = true)
    public PageResponse<ApplicationView> list(int page, int size) {
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM applications", Long.class);
        long totalElements = total == null ? 0 : total;

        String sql = "SELECT a.id, a.job_id, j.title AS job_title, a.candidate_name, a.email, "
                + "a.phone_encrypted, a.cover_letter, a.status, a.applied_at "
                + "FROM applications a JOIN jobs j ON j.id = a.job_id "
                + "ORDER BY a.applied_at DESC, a.id DESC LIMIT ? OFFSET ?";

        List<ApplicationView> rows = jdbc.query(sql, (rs, i) -> new ApplicationView(
                rs.getLong("id"),
                rs.getLong("job_id"),
                rs.getString("job_title"),
                rs.getString("candidate_name"),
                rs.getString("email"),
                encryptor.decrypt(rs.getString("phone_encrypted")),
                rs.getString("cover_letter"),
                rs.getString("status"),
                rs.getTimestamp("applied_at").toLocalDateTime()), size, (long) page * size);

        int totalPages = (int) Math.ceil(totalElements / (double) size);
        return new PageResponse<>(rows, page, size, totalElements, totalPages);
    }

    public void updateStatus(Long id, ApplicationStatus status) {
        JobApplication app = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id " + id));
        app.setStatus(status);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Application not found with id " + id);
        }
        repository.deleteById(id);
    }
}
