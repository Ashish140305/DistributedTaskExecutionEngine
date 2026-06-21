package com.taskengine.coordinator.service;

import com.taskengine.common.scheduler.SchedulingStrategy;
import com.taskengine.common.scheduler.SchedulingStrategy.WorkerSnapshot;
import com.taskengine.coordinator.grpc.WorkerGrpcClient;
import com.taskengine.coordinator.persistence.entity.TaskEntity;
import com.taskengine.coordinator.registry.WorkerRegistryService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Periodic task scheduling service.
 *
 * <p>Runs on a {@link ScheduledExecutorService} every 5 seconds to:
 * <ol>
 *     <li>Retrieve all PENDING and RETRYING tasks from the database</li>
 *     <li>Obtain active worker snapshots from the {@link WorkerRegistryService}</li>
 *     <li>Use the configured {@link SchedulingStrategy} to select target workers</li>
 *     <li>Issue gRPC {@code AssignTask} calls via {@link WorkerGrpcClient}</li>
 * </ol>
 *
 * <p>Task assignments are dispatched asynchronously using {@link CompletableFuture}
 * to avoid blocking the scheduler thread on slow gRPC calls.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Service
public class TaskSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(TaskSchedulerService.class);

    private static final long SCHEDULING_INTERVAL_SECONDS = 5;

    private final ScheduledExecutorService schedulerExecutor;
    private final TaskService taskService;
    private final WorkerRegistryService workerRegistry;
    private final SchedulingStrategy schedulingStrategy;
    private final WorkerGrpcClient workerGrpcClient;

    /**
     * Constructs the TaskSchedulerService.
     *
     * @param schedulerExecutor  the scheduled executor for periodic scheduling
     * @param taskService        the task service for querying pending tasks
     * @param workerRegistry     the worker registry for active worker snapshots
     * @param schedulingStrategy the scheduling strategy (round-robin by default)
     * @param workerGrpcClient   the gRPC client for worker communication
     */
    public TaskSchedulerService(
            @Qualifier("taskSchedulerExecutor") ScheduledExecutorService schedulerExecutor,
            TaskService taskService,
            WorkerRegistryService workerRegistry,
            @Qualifier("roundRobinScheduler") SchedulingStrategy schedulingStrategy,
            WorkerGrpcClient workerGrpcClient) {
        this.schedulerExecutor = schedulerExecutor;
        this.taskService = taskService;
        this.workerRegistry = workerRegistry;
        this.schedulingStrategy = schedulingStrategy;
        this.workerGrpcClient = workerGrpcClient;
    }

    /**
     * Starts the periodic scheduling loop after the bean is initialized.
     */
    @PostConstruct
    public void start() {
        log.info("Starting task scheduler with {}s interval using '{}' strategy",
                SCHEDULING_INTERVAL_SECONDS, schedulingStrategy.getName());

        schedulerExecutor.scheduleAtFixedRate(
                this::schedulePendingTasks,
                SCHEDULING_INTERVAL_SECONDS,
                SCHEDULING_INTERVAL_SECONDS,
                TimeUnit.SECONDS
        );
    }

    /**
     * Single scheduling cycle: fetches pending tasks and attempts to assign each
     * to an available worker.
     *
     * <p>This method is invoked by the scheduled executor. Exceptions are caught
     * to prevent the scheduled task from being cancelled on failure.
     */
    void schedulePendingTasks() {
        try {
            List<TaskEntity> pendingTasks = taskService.getPendingTasks();
            if (pendingTasks.isEmpty()) {
                log.debug("No pending tasks to schedule");
                return;
            }

            List<WorkerSnapshot> workerSnapshots = workerRegistry.getWorkerSnapshots();
            if (workerSnapshots.isEmpty()) {
                log.warn("No active workers available. {} tasks waiting for assignment.", pendingTasks.size());
                return;
            }

            log.info("Scheduling {} pending tasks across {} active workers",
                    pendingTasks.size(), workerSnapshots.size());

            int assigned = 0;
            for (TaskEntity task : pendingTasks) {
                Optional<WorkerSnapshot> selectedWorker = schedulingStrategy.selectWorker(workerSnapshots);

                if (selectedWorker.isEmpty()) {
                    log.debug("No workers with capacity for task '{}'. Stopping this scheduling cycle.", task.getId());
                    break;
                }

                WorkerSnapshot worker = selectedWorker.get();
                assignTaskAsync(task, worker);
                assigned++;
            }

            log.info("Dispatched {} task assignment(s) in this scheduling cycle", assigned);

        } catch (Exception e) {
            log.error("Error during task scheduling cycle", e);
        }
    }

    /**
     * Asynchronously assigns a task to a worker via gRPC.
     *
     * <p>Uses {@link CompletableFuture#runAsync} to perform the gRPC call
     * without blocking the scheduler thread.
     *
     * @param task   the task to assign
     * @param worker the target worker snapshot
     */
    private void assignTaskAsync(TaskEntity task, WorkerSnapshot worker) {
        CompletableFuture.runAsync(() -> {
            try {
                String workerId = worker.workerId();
                UUID taskId = task.getId();
                UUID workerUuid = UUID.fromString(workerId);

                // Update task status in DB
                taskService.assignTask(taskId, workerUuid);

                // Send gRPC assignment to worker
                boolean success = workerGrpcClient.assignTask(workerId, task);

                if (success) {
                    log.info("Successfully assigned task '{}' to worker '{}'", taskId, workerId);
                } else {
                    log.warn("Worker '{}' rejected task '{}'. Task will be re-queued.", workerId, taskId);
                    taskService.reassignTask(taskId);
                }
            } catch (Exception e) {
                log.error("Failed to assign task '{}' to worker '{}': {}",
                        task.getId(), worker.workerId(), e.getMessage(), e);
                try {
                    taskService.reassignTask(task.getId());
                } catch (Exception reassignEx) {
                    log.error("Failed to reassign task '{}' after assignment failure",
                            task.getId(), reassignEx);
                }
            }
        }, schedulerExecutor).exceptionally(throwable -> {
            log.error("Unhandled exception in async task assignment for task '{}'",
                    task.getId(), throwable);
            return null;
        });
    }
}
