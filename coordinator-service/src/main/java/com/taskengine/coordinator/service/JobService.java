package com.taskengine.coordinator.service;

import com.taskengine.common.dto.JobStatusResponse;
import com.taskengine.common.dto.JobSubmitRequest;
import com.taskengine.common.dto.TaskStatusResponse;
import com.taskengine.common.exception.JobNotFoundException;
import com.taskengine.common.model.JobStatus;
import com.taskengine.common.model.TaskStatus;
import com.taskengine.common.util.IdGenerator;
import com.taskengine.coordinator.event.JobStateChangeEvent;
import com.taskengine.coordinator.persistence.entity.JobEntity;
import com.taskengine.coordinator.persistence.entity.TaskEntity;
import com.taskengine.coordinator.persistence.repository.JobRepository;
import com.taskengine.coordinator.persistence.repository.TaskRepository;
import com.taskengine.coordinator.splitter.DefaultJobSplitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Core service for job lifecycle management.
 *
 * <p>Handles the complete job lifecycle from submission through completion:
 * <ol>
 *     <li>Accepts job submissions and persists them</li>
 *     <li>Splits job input into individual tasks via {@link DefaultJobSplitter}</li>
 *     <li>Tracks job progress by aggregating task statuses</li>
 *     <li>Publishes {@link JobStateChangeEvent}s on state transitions</li>
 * </ol>
 *
 * <p>Per-job locking is used to ensure that concurrent task completion callbacks
 * do not produce inconsistent job-level state.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Service
@Transactional
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepository;
    private final TaskRepository taskRepository;
    private final DefaultJobSplitter jobSplitter;
    private final TaskService taskService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Per-job locks to serialize concurrent updates to the same job's aggregate state.
     * Keys are job UUID strings.
     */
    private final ConcurrentHashMap<String, ReentrantLock> jobLocks = new ConcurrentHashMap<>();

    /**
     * Constructs the JobService with required dependencies.
     *
     * @param jobRepository  the job repository
     * @param taskRepository the task repository
     * @param jobSplitter    the default job splitter
     * @param taskService    the task service for creating tasks
     * @param eventPublisher the Spring event publisher
     */
    public JobService(JobRepository jobRepository,
                      TaskRepository taskRepository,
                      DefaultJobSplitter jobSplitter,
                      TaskService taskService,
                      ApplicationEventPublisher eventPublisher) {
        this.jobRepository = jobRepository;
        this.taskRepository = taskRepository;
        this.jobSplitter = jobSplitter;
        this.taskService = taskService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Submits a new job for execution.
     *
     * <p>Creates a {@link JobEntity}, splits the input into task chunks using
     * the job splitter, creates individual {@link TaskEntity} records, and
     * persists everything in a single transaction.
     *
     * @param request the job submission request containing name, type, and input data
     * @return the status response for the newly created job
     */
    public JobStatusResponse submitJob(JobSubmitRequest request) {
        log.info("Submitting new job: name='{}', type='{}'", request.getName(), request.getType());

        // Create and persist the job entity
        JobEntity jobEntity = new JobEntity();
        jobEntity.setId(IdGenerator.generateUUID());
        jobEntity.setName(request.getName());
        jobEntity.setType(request.getType());
        jobEntity.setStatus(JobStatus.SUBMITTED);
        jobEntity.setInputData(request.getInputData());

        // Split input into task chunks
        List<String> inputChunks = jobSplitter.split(request.getInputData());
        jobEntity.setTotalTasks(inputChunks.size());
        jobEntity.setCompletedTasks(0);
        jobEntity.setFailedTasks(0);

        jobEntity = jobRepository.save(jobEntity);
        log.info("Created job '{}' with ID '{}'", jobEntity.getName(), jobEntity.getId());

        // Create task entities for each chunk
        taskService.createTasksForJob(jobEntity, inputChunks);

        // Transition to RUNNING
        JobStatus oldStatus = jobEntity.getStatus();
        jobEntity.setStatus(JobStatus.RUNNING);
        jobEntity = jobRepository.save(jobEntity);

        eventPublisher.publishEvent(new JobStateChangeEvent(
                this, jobEntity.getId().toString(), oldStatus, JobStatus.RUNNING));

        log.info("Job '{}' submitted successfully with {} tasks", jobEntity.getId(), inputChunks.size());

        return buildJobStatusResponse(jobEntity);
    }

    /**
     * Retrieves the current status of a job including its tasks.
     *
     * @param jobId the job's UUID string
     * @return the job status response
     * @throws JobNotFoundException if the job does not exist
     */
    @Transactional(readOnly = true)
    public JobStatusResponse getJobStatus(String jobId) {
        log.debug("Retrieving status for job '{}'", jobId);

        JobEntity jobEntity = jobRepository.findById(UUID.fromString(jobId))
                .orElseThrow(() -> new JobNotFoundException("Job not found: " + jobId));

        return buildJobStatusResponse(jobEntity);
    }

    /**
     * Cancels a job and all its non-terminal tasks.
     *
     * <p>Tasks that are already COMPLETED or FAILED are left unchanged.
     * Tasks in PENDING, ASSIGNED, RUNNING, or RETRYING state are set to CANCELLED.
     *
     * @param jobId the job's UUID string
     * @return the updated job status response
     * @throws JobNotFoundException if the job does not exist
     */
    public JobStatusResponse cancelJob(String jobId) {
        log.info("Cancelling job '{}'", jobId);

        JobEntity jobEntity = jobRepository.findById(UUID.fromString(jobId))
                .orElseThrow(() -> new JobNotFoundException("Job not found: " + jobId));

        if (jobEntity.getStatus() == JobStatus.COMPLETED ||
                jobEntity.getStatus() == JobStatus.CANCELLED ||
                jobEntity.getStatus() == JobStatus.FAILED) {
            log.warn("Job '{}' is already in terminal state '{}', cannot cancel",
                    jobId, jobEntity.getStatus());
            return buildJobStatusResponse(jobEntity);
        }

        // Cancel all non-terminal tasks
        List<TaskEntity> tasks = taskRepository.findByJobId(UUID.fromString(jobId));
        Set<TaskStatus> nonTerminalStatuses = EnumSet.of(
                TaskStatus.PENDING, TaskStatus.ASSIGNED, TaskStatus.RUNNING, TaskStatus.RETRYING);

        int cancelledCount = 0;
        for (TaskEntity task : tasks) {
            if (nonTerminalStatuses.contains(task.getStatus())) {
                task.setStatus(TaskStatus.CANCELLED);
                task.setCompletedAt(LocalDateTime.now());
                taskRepository.save(task);
                cancelledCount++;
            }
        }

        // Update job status
        JobStatus oldStatus = jobEntity.getStatus();
        jobEntity.setStatus(JobStatus.CANCELLED);
        jobEntity.setCompletedAt(LocalDateTime.now());
        jobEntity = jobRepository.save(jobEntity);

        eventPublisher.publishEvent(new JobStateChangeEvent(
                this, jobId, oldStatus, JobStatus.CANCELLED));

        log.info("Job '{}' cancelled. {} tasks cancelled.", jobId, cancelledCount);

        return buildJobStatusResponse(jobEntity);
    }

    /**
     * Lists all jobs in the system.
     *
     * @return list of job status responses
     */
    @Transactional(readOnly = true)
    public List<JobStatusResponse> getAllJobs() {
        log.debug("Listing all jobs");
        return jobRepository.findAll().stream()
                .map(this::buildJobStatusResponse)
                .toList();
    }

    /**
     * Checks whether all tasks for a job have reached a terminal state and updates
     * the job status accordingly.
     *
     * <p>This method is called after each task completion or failure. It uses per-job
     * locking to prevent race conditions when multiple tasks complete simultaneously.
     *
     * @param jobId the job's UUID string
     */
    public void checkJobCompletion(String jobId) {
        ReentrantLock lock = jobLocks.computeIfAbsent(jobId, k -> new ReentrantLock());
        lock.lock();
        try {
            JobEntity jobEntity = jobRepository.findById(UUID.fromString(jobId)).orElse(null);
            if (jobEntity == null || jobEntity.getStatus() == JobStatus.COMPLETED
                    || jobEntity.getStatus() == JobStatus.FAILED
                    || jobEntity.getStatus() == JobStatus.CANCELLED) {
                return;
            }

            UUID jobUuid = UUID.fromString(jobId);
            long completedCount = taskRepository.countByJobIdAndStatus(jobUuid, TaskStatus.COMPLETED);
            long failedCount = taskRepository.countByJobIdAndStatus(jobUuid, TaskStatus.FAILED);
            long cancelledCount = taskRepository.countByJobIdAndStatus(jobUuid, TaskStatus.CANCELLED);
            long totalTasks = jobEntity.getTotalTasks();

            // Update aggregate counts
            jobEntity.setCompletedTasks((int) completedCount);
            jobEntity.setFailedTasks((int) failedCount);

            long terminalCount = completedCount + failedCount + cancelledCount;

            if (terminalCount >= totalTasks) {
                JobStatus oldStatus = jobEntity.getStatus();
                JobStatus newStatus;

                if (failedCount > 0) {
                    newStatus = JobStatus.FAILED;
                } else {
                    newStatus = JobStatus.COMPLETED;
                }

                jobEntity.setStatus(newStatus);
                jobEntity.setCompletedAt(LocalDateTime.now());
                jobRepository.save(jobEntity);

                eventPublisher.publishEvent(new JobStateChangeEvent(this, jobId, oldStatus, newStatus));

                log.info("Job '{}' reached terminal state '{}'. Completed: {}, Failed: {}, Cancelled: {}",
                        jobId, newStatus, completedCount, failedCount, cancelledCount);
            } else {
                jobRepository.save(jobEntity);
                log.debug("Job '{}' progress: {}/{} tasks terminal (completed={}, failed={}, cancelled={})",
                        jobId, terminalCount, totalTasks, completedCount, failedCount, cancelledCount);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Builds a {@link JobStatusResponse} from a job entity and its tasks.
     */
    private JobStatusResponse buildJobStatusResponse(JobEntity jobEntity) {
        List<TaskEntity> tasks = taskRepository.findByJobId(jobEntity.getId());
        List<TaskStatusResponse> taskResponses = tasks.stream()
                .map(this::buildTaskStatusResponse)
                .toList();

        JobStatusResponse response = new JobStatusResponse();
        response.setJobId(jobEntity.getId().toString());
        response.setName(jobEntity.getName());
        response.setType(jobEntity.getType());
        response.setStatus(jobEntity.getStatus());
        response.setTotalTasks(jobEntity.getTotalTasks());
        response.setCompletedTasks(jobEntity.getCompletedTasks());
        response.setFailedTasks(jobEntity.getFailedTasks());
        response.setCreatedAt(jobEntity.getCreatedAt());
        response.setUpdatedAt(jobEntity.getUpdatedAt());
        response.setCompletedAt(jobEntity.getCompletedAt());
        response.setTasks(taskResponses);
        return response;
    }

    /**
     * Builds a {@link TaskStatusResponse} from a task entity.
     */
    private TaskStatusResponse buildTaskStatusResponse(TaskEntity taskEntity) {
        TaskStatusResponse response = new TaskStatusResponse();
        response.setTaskId(taskEntity.getId().toString());
        response.setJobId(taskEntity.getJob().getId().toString());
        response.setStatus(taskEntity.getStatus());
        response.setType(taskEntity.getType());
        response.setWorkerId(taskEntity.getWorker() != null ? taskEntity.getWorker().getId().toString() : null);
        response.setRetryCount(taskEntity.getRetryCount());
        response.setMaxRetries(taskEntity.getMaxRetries());
        response.setErrorMessage(taskEntity.getErrorMessage());
        response.setResultData(taskEntity.getResultData());
        response.setCreatedAt(taskEntity.getCreatedAt());
        response.setUpdatedAt(taskEntity.getUpdatedAt());
        response.setStartedAt(taskEntity.getStartedAt());
        response.setCompletedAt(taskEntity.getCompletedAt());
        return response;
    }
}
