package com.jobportal.config;

import com.jobportal.entity.AppUser;
import com.jobportal.entity.Job;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Creates the default admin user and a few sample jobs on first start. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final JobRepository jobs;
    private final PasswordEncoder encoder;
    private final String adminUser;
    private final String adminPass;

    public DataSeeder(UserRepository users, JobRepository jobs, PasswordEncoder encoder,
                      @Value("${app.admin.username}") String adminUser,
                      @Value("${app.admin.password}") String adminPass) {
        this.users = users;
        this.jobs = jobs;
        this.encoder = encoder;
        this.adminUser = adminUser;
        this.adminPass = adminPass;
    }

    @Override
    public void run(String... args) {
        if (users.findByUsername(adminUser).isEmpty()) {
            users.save(new AppUser(adminUser, encoder.encode(adminPass), "ADMIN"));
        }
        if (jobs.count() == 0) {
            jobs.save(job("Senior Java Developer", "TechNova Pvt Ltd", "Mumbai", "Full-time", "18-28 LPA",
                    "Build scalable Spring Boot microservices, design REST APIs and optimise MySQL queries."));
            jobs.save(job("Backend Engineer (Spring Boot)", "FinServe Solutions", "Pune", "Full-time", "12-20 LPA",
                    "Work on high-volume financial data pipelines using Java, JPA and Kafka."));
            jobs.save(job("Java Full Stack Developer", "CloudNest", "Remote", "Contract", "80-100 / hour",
                    "Spring Boot backend with HTML, JavaScript and AJAX based frontend."));
            jobs.save(job("DevOps Engineer", "InfraWorks", "Bengaluru", "Hybrid", "15-25 LPA",
                    "Docker, Kubernetes, Azure DevOps CI/CD pipelines and monitoring."));
        }
    }

    private Job job(String title, String company, String location, String type, String salary, String desc) {
        Job j = new Job();
        j.setTitle(title);
        j.setCompany(company);
        j.setLocation(location);
        j.setJobType(type);
        j.setSalary(salary);
        j.setDescription(desc);
        return j;
    }
}
