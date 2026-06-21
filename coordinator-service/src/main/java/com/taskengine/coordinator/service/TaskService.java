package com.taskengine.coordinator.service;

import com.taskengine.common.model.TaskStatus;
import com.taskengine.common.util.IdGenerator;
import com.taskengine.coordinator.config.MetricsConfig;
import com.taskengine.coordinator.persistence.entity.JobEntity;
import com.taskengine.coordinator.persistence.entity.TaskEntity;
import com.taskengine.coordinator.persistence.entity.TaskResultEntity;
import com.taskengine.coordinator.persistence.entity.WorkerEntity;
import com.taskengine.coordinator.persistence.repository.TaskRepository;
import com.taskengine.coordinator.persistence.repository.TaskResultRepository;
import com.taskengine.coordinator.persistence.repository.WorkerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for task lifecycle management.
 *
 * <p>Manages individual task state transitions within a job:
 * <ul>
 *     <li>Creating tasks from job input chunks</li>
 *     <li>Assigning tasks to workers</li>
 *     <li>Recording task completion and failure</li>
 *     <li>Handling task retries up to the configured maximum</li>
 *     <li>Reassigning tasks from dead workers</li>
 * </ul>
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Service
@Transactional
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private static final int DEFAULT_MAX_RETRIES = 3;

    private final TaskRepository taskRepository;
    private final TaskResultRepository taskResultRepository;
    private final WorkerRepository workerRepository;
    private final MetricsConfig.MetricsRegistrar metricsRegistrar;

    /**
     * Constructs the TaskService with required dependencies.
     *
     * @param taskRepository       the task repository
     * @param taskResultRepository the task result repository
     * @param workerRepository     the worker repository
     * @param metricsRegistrar     the metrics registrar for recording counters/timers
     */
    public TaskService(TaskRepository taskRepository,
                       TaskResultRepository taskResultRepository,
                       WorkerRepository workerRepository,
                       MetricsConfig.MetricsRegistrar metricsRegistrar) {
        this.taskRepository = taskRepository;
        this.taskResultRepository = taskResultRepository;
        this.workerRepository = workerRepository;
        this.metricsRegistrar = metricsRegistrar;
    }

    /**
     * Creates task entities for each input chunk of a job.
     *
     * <p>Each chunk becomes a separate task with PENDING status. All tasks share
     * the job's type and default retry configuration.
     *
     * @param jobEntity   the parent job entity
     * @param inputChunks the list of individual task input strings
     * @return the list of persisted task entities
     */
    public List<TaskEntity> createTasksForJob(JobEntity jobEntity, List<String> inputChunks) {
        log.info("Creating {} tasks for job '{}'", inputChunks.size(), jobEntity.getId());

        List<TaskEntity> tasks = new ArrayList<>();
        for (String chunk : inputChunks) {
            TaskEntity task = new TaskEntity();
            task.setId(IdGenerator.generateUUID());
            task.setJob(jobEntity);
            task.setStatus(TaskStatus.PENDING);
            task.setType(jobEntity.getType());
            task.setInputData(chunk);
            task.setRetryCount(0);
            task.setMaxRetries(DEFAULT_MAX_RETRIES);
            tasks.add(task);
        }

        List<TaskEntity> savedTasks = taskRepository.saveAll(tasks);
        log.info("Created {} tasks for job '{}'", savedTasks.size(), jobEntity.getId());

        return savedTasks;
    }

    /**
     * Assigns a task to a specific worker.
     *
     * <p>Sets the task status to ASSIGNED, records the worker reference,
     * and updates the started timestamp.
     *
     * @param taskId   the task's UUID
     * @param workerId the worker's UUID
     * @return the updated task entity, or empty if the task was not found
     */
    public Optional<TaskEntity> assignTask(UUID taskId, UUID workerId) {
        log.info("Assigning task '{}' to worker '{}'", taskId, workerId);

        Optional<TaskEntity> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            log.warn("Task '{}' not found for assignment", taskId);
            return Optional.empty();
        }

        TaskEntity task = taskOpt.get();

        // Only assign if the task is in an assignable state
        if (task.getStatus() != TaskStatus.PENDING && task.getStatus() != TaskStatus.RETRYING) {
            log.warn("Task '{}' is in state '{}', cannot assign", taskId, task.getStatus());
            return Optional.of(task);
        }

        Optional<WorkerEntity> workerOpt = workerRepository.findById(workerId);
        if (workerOpt.isEmpty()) {
            log.warn("Worker '{}' not found for task assignment", workerId);
            return Optional.of(task);
        }

        task.setWorker(workerOpt.get());
        task.setStatus(TaskStatus.ASSIGNED);
        task.setStartedAt(LocalDateTime.now());
        task = taskRepository.save(task);

        log.info("Task '{}' assigned to worker '{}'", taskId, workerId);
        return Optional.of(task);
    }

    /**
     * Marks a task as completed with its result data.
     *
     * <p>Creates a {@link TaskResultEntity} to record the successful execution
     * attempt and triggers a job completion check.
     *
     * @param taskId          the task's UUID string
     * @param resultData      the output data produced by the task
     * @param executionTimeMs the execution duration in milliseconds
     */
    public UUID completeTask(String taskId, String resultData, long executionTimeMs) {
        log.info("Completing task '{}'. Execution time: {}ms", taskId, executionTimeMs);

        TaskEntity task = taskRepository.findById(UUID.fromString(taskId))
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        task.setStatus(TaskStatus.COMPLETED);
        task.setResultData(resultData);
        task.setCompletedAt(LocalDateTime.now());
        taskRepository.save(task);

        // Save task result record
        saveTaskResult(task, resultData, executionTimeMs, "COMPLETED", null);

        // Record metrics
        metricsRegistrar.recordTaskCompleted();
        metricsRegistrar.recordTaskExecutionTime(executionTimeMs);

        log.info("Task '{}' completed successfully. Job: '{}'", taskId, task.getJob().getId());
        return task.getJob().getId();
    }

    /**
     * Records a task failure and determines whether to retry or mark as permanently failed.
     *
     * <p>If the task's retry count is below the maximum, the task is set to RETRYING
     * and the retry count is incremented. Otherwise, it is set to FAILED.
     *
     * @param taskId          the task's UUID string
     * @param errorMessage    the error description
     * @param executionTimeMs the execution duration in milliseconds
     */
    public UUID failTask(String taskId, String errorMessage, long executionTimeMs) {
        log.warn("Task '{}' failed: {}", taskId, errorMessage);

        TaskEntity task = taskRepository.findById(UUID.fromString(taskId))
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        int newRetryCount = task.getRetryCount() + 1;
        task.setRetryCount(newRetryCount);
        task.setErrorMessage(errorMessage);

        String resultStatus;
        if (newRetryCount < task.getMaxRetries()) {
            task.setStatus(TaskStatus.RETRYING);
            task.setWorker(null);
            task.setStartedAt(null);
            resultStatus = "RETRYING";
            metricsRegistrar.recordTaskRetry();
            log.info("Task '{}' will be retried (attempt {}/{})", taskId, newRetryCount, task.getMaxRetries());
        } else {
            task.setStatus(TaskStatus.FAILED);
            task.setCompletedAt(LocalDateTime.now());
            resultStatus = "FAILED";
            metricsRegistrar.recordTaskFailed();
            log.error("Task '{}' permanently failed after {} attempts", taskId, newRetryCount);
        }

        taskRepository.save(task);

        // Save task result record
        saveTaskResult(task, null, executionTimeMs, resultStatus, errorMessage);

        if (executionTimeMs > 0) {
            metricsRegistrar.recordTaskExecutionTime(executionTimeMs);
        }
        
        return task.getJob().getId();
    }

    /**
     * Returns all tasks in PENDING or RETRYING status that are available for scheduling.
     *
     * @return list of pending/retrying tasks
     */
    @Transactional(readOnly = true)
    public List<TaskEntity> getPendingTasks() {
        return taskRepository.findByStatusIn(List.of(TaskStatus.PENDING, TaskStatus.RETRYING));
    }

    /**
     * Returns all tasks assigned to a specific worker with any of the given statuses.
     *
     * @param workerId the worker's UUID
     * @param statuses the statuses to match
     * @return list of matching tasks
     */
    @Transactional(readOnly = true)
    public List<TaskEntity> getTasksByWorker(UUID workerId, Collection<TaskStatus> statuses) {
        return taskRepository.findByWorkerIdAndStatusIn(workerId, statuses);
    }

    /**
     * Reassigns a task by clearing its worker and resetting to an assignable state.
     *
     * <p>Used during fault recovery when a worker dies. The task's worker reference
     * is cleared so the scheduler can pick it up for re-assignment.
     *
     * @param taskId the task's UUID
     */
    public void reassignTask(UUID taskId) {
        log.info("Reassigning task '{}'", taskId);

        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        int newRetryCount = task.getRetryCount() + 1;
        task.setRetryCount(newRetryCount);
        task.setWorker(null);
        task.setStartedAt(null);

        if (newRetryCount < task.getMaxRetries()) {
            task.setStatus(TaskStatus.RETRYING);
            metricsRegistrar.recordTaskRetry();
            log.info("Task '{}' reset to RETRYING for re-assignment (attempt {}/{})",
                    taskId, newRetryCount, task.getMaxRetries());
        } else {
            task.setStatus(TaskStatus.FAILED);
            task.setCompletedAt(LocalDateTime.now());
            metricsRegistrar.recordTaskFailed();
            log.error("Task '{}' permanently failed — max retries ({}) exceeded during reassignment",
                    taskId, task.getMaxRetries());
        }

        taskRepository.save(task);
    }

    /**
     * Persists a task result record for audit and diagnostics.
     */
    private void saveTaskResult(TaskEntity task, String resultData, long executionTimeMs,
                                String status, String errorMessage) {
        TaskResultEntity result = new TaskResultEntity();
        result.setId(IdGenerator.generateUUID());
        result.setTask(task);
        result.setResultData(resultData);
        result.setExecutionTimeMs(executionTimeMs);
        result.setAttemptNumber(task.getRetryCount() + 1);
        result.setStatus(status);
        result.setErrorMessage(errorMessage);
        taskResultRepository.save(result);
    }
}
