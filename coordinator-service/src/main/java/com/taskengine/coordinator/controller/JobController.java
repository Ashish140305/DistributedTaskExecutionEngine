package com.taskengine.coordinator.controller;

import com.taskengine.common.dto.JobStatusResponse;
import com.taskengine.common.dto.JobSubmitRequest;
import com.taskengine.coordinator.service.JobService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for job management operations.
 *
 * <p>Provides the primary HTTP API for external clients to:
 * <ul>
 *   <li>Submit new jobs</li>
 *   <li>Query job status and progress</li>
 *   <li>List all jobs</li>
 *   <li>Cancel running jobs</li>
 * </ul>
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Submits a new job for distributed execution.
     *
     * <p>The job input data is split into tasks by the configured job splitter
     * and distributed across available workers.
     *
     * @param request the job submission request
     * @return the created job's status (201 Created)
     */
    @PostMapping
    public ResponseEntity<JobStatusResponse> submitJob(@RequestBody JobSubmitRequest request) {
        log.info("REST: Submitting job: name='{}', type='{}'", request.getName(), request.getType());

        JobStatusResponse response = jobService.submitJob(request);

        log.info("REST: Job created: jobId={}, totalTasks={}", response.getJobId(), response.getTotalTasks());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves the current status of a job including all its tasks.
     *
     * @param jobId the job's UUID
     * @return the job status response (200 OK)
     */
    @GetMapping("/{jobId}")
    public ResponseEntity<JobStatusResponse> getJobStatus(@PathVariable("jobId") String jobId) {
        log.debug("REST: Getting status for job '{}'", jobId);

        JobStatusResponse response = jobService.getJobStatus(jobId);
        return ResponseEntity.ok(response);
    }

    /**
     * Lists all jobs in the system.
     *
     * @return list of all job status responses (200 OK)
     */
    @GetMapping
    public ResponseEntity<List<JobStatusResponse>> getAllJobs() {
        log.debug("REST: Listing all jobs");

        List<JobStatusResponse> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    /**
     * Cancels a running job and all its non-terminal tasks.
     *
     * @param jobId the job's UUID
     * @return the updated job status (200 OK)
     */
    @DeleteMapping("/{jobId}")
    public ResponseEntity<JobStatusResponse> cancelJob(@PathVariable("jobId") String jobId) {
        log.info("REST: Cancelling job '{}'", jobId);

        JobStatusResponse response = jobService.cancelJob(jobId);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint for the coordinator.
     *
     * @return simple health status map
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "coordinator"
        ));
    }
}
