package com.taskengine.coordinator.fault;

import com.taskengine.common.model.JobStatus;
import com.taskengine.common.model.TaskStatus;
import com.taskengine.coordinator.persistence.entity.JobEntity;
import com.taskengine.coordinator.persistence.entity.TaskEntity;
import com.taskengine.coordinator.persistence.repository.JobRepository;
import com.taskengine.coordinator.persistence.repository.TaskRepository;
import com.taskengine.coordinator.registry.WorkerRegistryService;
import com.taskengine.coordinator.service.JobService;
import com.taskengine.coordinator.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service responsible for fault recovery operations.
 *
 * <p>Handles two key recovery scenarios:
 * <ol>
 *     <li><b>Dead worker recovery:</b> When the heartbeat monitor detects a dead worker,
 *         this service reassigns all in-flight tasks from that worker and cleans up
 *         the gRPC channel and registry entry.</li>
 *     <li><b>Coordinator restart recovery:</b> On startup, re-queues all tasks that
 *         were in non-terminal states from unfinished jobs, ensuring no work is lost
 *         across coordinator restarts.</li>
 * </ol>
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Service
public class FaultRecoveryService {

    private static final Logger log = LoggerFactory.getLogger(FaultRecoveryService.class);

    private final TaskService taskService;
    private final JobService jobService;
    private final TaskRepository taskRepository;
    private final JobRepository jobRepository;
    private final WorkerRegistryService workerRegistry;

    /**
     * Constructs the fault recovery service.
     *
     * @param taskService    the task service for reassignment operations
     * @param jobService     the job service for completion checks
     * @param taskRepository the task repository for direct queries
     * @param jobRepository  the job repository for finding unfinished jobs
     * @param workerRegistry the worker registry for cleanup
     */
    public FaultRecoveryService(TaskService taskService,
                                JobService jobService,
                                TaskRepository taskRepository,
                                JobRepository jobRepository,
                                WorkerRegistryService workerRegistry) {
        this.taskService = taskService;
        this.jobService = jobService;
        this.taskRepository = taskRepository;
        this.jobRepository = jobRepository;
        this.workerRegistry = workerRegistry;
    }

    /**
     * Handles recovery for a dead worker.
     *
     * <p>Finds all ASSIGNED and RUNNING tasks on the dead worker, reassigns each
     * (incrementing retry count), closes the gRPC channel, and removes the worker
     * from the in-memory registry.
     *
     * @param workerId the dead worker's UUID string
     */
    @Transactional
    public void handleDeadWorker(String workerId) {
        log.info("Handling dead worker '{}'. Recovering in-flight tasks.", workerId);

        Set<TaskStatus> inFlightStatuses = EnumSet.of(TaskStatus.ASSIGNED, TaskStatus.RUNNING);
        List<TaskEntity> affectedTasks = taskService.getTasksByWorker(
                UUID.fromString(workerId), inFlightStatuses);

        log.info("Found {} in-flight task(s) on dead worker '{}'", affectedTasks.size(), workerId);

        int reassigned = 0;
        int failed = 0;
        Set<String> affectedJobIds = new java.util.HashSet<>();

        for (TaskEntity task : affectedTasks) {
            try {
                taskService.reassignTask(task.getId());
                affectedJobIds.add(task.getJob().getId().toString());

                if (task.getRetryCount() + 1 < task.getMaxRetries()) {
                    reassigned++;
                } else {
                    failed++;
                }
            } catch (Exception e) {
                log.error("Failed to reassign task '{}' from dead worker '{}'",
                        task.getId(), workerId, e);
                failed++;
            }
        }

        // Remove worker from registry (closes gRPC channel)
        workerRegistry.removeWorker(workerId);

        // Check job completion for all affected jobs
        for (String jobId : affectedJobIds) {
            try {
                jobService.checkJobCompletion(jobId);
            } catch (Exception e) {
                log.error("Failed to check job completion for job '{}' after worker death", jobId, e);
            }
        }

        log.info("Dead worker '{}' recovery complete. Tasks reassigned: {}, Tasks permanently failed: {}",
                workerId, reassigned, failed);
    }

    /**
     * Recovers unfinished jobs and tasks after a coordinator restart.
     *
     * <p>Called once on startup by {@link com.taskengine.coordinator.recovery.CoordinatorRecoveryService}.
     * Finds all jobs in SUBMITTED or RUNNING state and ensures their non-terminal tasks
     * are re-queued for scheduling.
     *
     * @return a summary record of the recovery operation
     */
    @Transactional
    public RecoverySummary recoverUnfinishedJobs() {
        log.info("Starting coordinator restart recovery — scanning for unfinished jobs and tasks");

        List<JobEntity> unfinishedJobs = jobRepository.findByStatusIn(
                List.of(JobStatus.SUBMITTED, JobStatus.RUNNING));

        int totalJobsRecovered = 0;
        int totalTasksReQueued = 0;
        int totalTasksFailed = 0;

        for (JobEntity job : unfinishedJobs) {
            UUID jobId = job.getId();
            log.info("Recovering job '{}' (name='{}', status={})",
                    jobId, job.getName(), job.getStatus());

            // Find tasks in non-terminal states
            Set<TaskStatus> nonTerminalStatuses = EnumSet.of(
                    TaskStatus.PENDING, TaskStatus.ASSIGNED, TaskStatus.RUNNING, TaskStatus.RETRYING);

            List<TaskEntity> tasksToRecover = taskRepository.findByJobId(jobId).stream()
                    .filter(t -> nonTerminalStatuses.contains(t.getStatus()))
                    .toList();

            for (TaskEntity task : tasksToRecover) {
                try {
                    // Reset ASSIGNED and RUNNING tasks back to their assignable state
                    if (task.getStatus() == TaskStatus.ASSIGNED || task.getStatus() == TaskStatus.RUNNING) {
                        task.setWorker(null);
                        task.setStartedAt(null);

                        if (task.getRetryCount() < task.getMaxRetries()) {
                            task.setStatus(TaskStatus.RETRYING);
                            totalTasksReQueued++;
                        } else {
                            task.setStatus(TaskStatus.FAILED);
                            task.setErrorMessage("Failed during coordinator restart recovery");
                            totalTasksFailed++;
                        }
                        taskRepository.save(task);
                    } else {
                        // PENDING and RETRYING tasks are already in an assignable state
                        totalTasksReQueued++;
                    }
                } catch (Exception e) {
                    log.error("Failed to recover task '{}' from job '{}'", task.getId(), jobId, e);
                    totalTasksFailed++;
                }
            }

            // Ensure job is in RUNNING state so tasks get scheduled
            if (job.getStatus() == JobStatus.SUBMITTED) {
                job.setStatus(JobStatus.RUNNING);
                jobRepository.save(job);
            }

            // Check if job is already complete (all tasks terminal)
            jobService.checkJobCompletion(jobId.toString());

            totalJobsRecovered++;
        }

        RecoverySummary summary = new RecoverySummary(
                totalJobsRecovered, totalTasksReQueued, totalTasksFailed);

        log.info("Coordinator restart recovery complete: {}", summary);

        return summary;
    }

    /**
     * Summary record of a recovery operation.
     *
     * @param jobsRecovered  number of jobs found and processed
     * @param tasksReQueued  number of tasks re-queued for scheduling
     * @param tasksFailed    number of tasks that permanently failed
     */
    public record RecoverySummary(int jobsRecovered, int tasksReQueued, int tasksFailed) {
        @Override
        public String toString() {
            return "RecoverySummary{" +
                    "jobsRecovered=" + jobsRecovered +
                    ", tasksReQueued=" + tasksReQueued +
                    ", tasksFailed=" + tasksFailed +
                    '}';
        }
    }
}
