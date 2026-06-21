package com.taskengine.worker.engine;

import com.taskengine.common.executor.TaskExecutor;
import com.taskengine.common.model.TaskContext;
import com.taskengine.common.model.TaskResult;
import com.taskengine.worker.executor.TaskExecutorFactory;
import com.taskengine.worker.grpc.CoordinatorGrpcClient;
import com.taskengine.worker.metrics.WorkerMetricsService;
import com.taskengine.proto.AssignTaskRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Core task execution engine for worker nodes.
 *
 * <p>Manages a {@link ThreadPoolExecutor} to execute tasks concurrently.
 * Tracks running tasks in a {@link ConcurrentHashMap} and reports results
 * back to the coordinator via gRPC.
 *
 * <p><b>Concurrency:</b>
 * <ul>
 *   <li>{@code ConcurrentHashMap} for thread-safe running task tracking</li>
 *   <li>{@code AtomicInteger} for lock-free running task count</li>
 *   <li>{@code CompletableFuture} for async result handling with callbacks</li>
 * </ul>
 */
@Service
public class TaskExecutionEngine {

    private static final Logger log = LoggerFactory.getLogger(TaskExecutionEngine.class);

    private final ThreadPoolExecutor taskExecutorPool;
    private final TaskExecutorFactory taskExecutorFactory;
    private final CoordinatorGrpcClient coordinatorClient;
    private final WorkerMetricsService metricsService;

    private final ConcurrentHashMap<String, Future<?>> runningTasks = new ConcurrentHashMap<>();
    private final AtomicInteger runningTaskCount = new AtomicInteger(0);

    public TaskExecutionEngine(ThreadPoolExecutor taskExecutorPool,
                               TaskExecutorFactory taskExecutorFactory,
                               CoordinatorGrpcClient coordinatorClient,
                               WorkerMetricsService metricsService) {
        this.taskExecutorPool = taskExecutorPool;
        this.taskExecutorFactory = taskExecutorFactory;
        this.coordinatorClient = coordinatorClient;
        this.metricsService = metricsService;
    }

    /**
     * Submits a task for asynchronous execution.
     *
     * <p>Creates a {@link TaskContext} from the gRPC request, retrieves the
     * appropriate executor from the factory, and submits to the thread pool.
     * Completion and failure callbacks automatically report results to the coordinator.
     *
     * @param request the gRPC task assignment request
     * @return true if the task was accepted for execution
     */
    public boolean submitTask(AssignTaskRequest request) {
        String taskId = request.getTaskId();
        String taskType = request.getType();

        log.info("Submitting task for execution: taskId={}, type={}, jobId={}, retryCount={}",
                taskId, taskType, request.getJobId(), request.getRetryCount());

        // Build task context from the gRPC request
        TaskContext context = TaskContext.builder()
                .taskId(taskId)
                .jobId(request.getJobId())
                .taskType(taskType)
                .inputData(request.getInputData())
                .parameters(request.getParametersMap())
                .retryCount(request.getRetryCount())
                .maxRetries(request.getMaxRetries())
                .build();

        // Get the appropriate executor for this task type
        TaskExecutor executor = taskExecutorFactory.getExecutor(taskType);

        // Submit to thread pool as CompletableFuture
        CompletableFuture<TaskResult> future = CompletableFuture.supplyAsync(() -> {
            runningTaskCount.incrementAndGet();
            long startTime = System.currentTimeMillis();
            try {
                log.info("Executing task: taskId={}, type={}", taskId, taskType);
                TaskResult result = executor.execute(context);
                long executionTime = System.currentTimeMillis() - startTime;
                log.info("Task completed successfully: taskId={}, executionTimeMs={}", taskId, executionTime);
                return result;
            } catch (Exception e) {
                long executionTime = System.currentTimeMillis() - startTime;
                log.error("Task execution failed: taskId={}, error={}, executionTimeMs={}",
                        taskId, e.getMessage(), executionTime, e);
                return TaskResult.failure(taskId, e.getMessage(), executionTime, context.getRetryCount() + 1);
            }
        }, taskExecutorPool);

        // Handle completion — report to coordinator
        future.whenComplete((result, throwable) -> {
            runningTaskCount.decrementAndGet();
            runningTasks.remove(taskId);

            if (throwable != null) {
                // Unexpected error (e.g., thread pool rejection)
                log.error("Unexpected error during task execution: taskId={}", taskId, throwable);
                coordinatorClient.reportTaskFailure(
                        coordinatorClient.getWorkerId(),
                        taskId,
                        throwable.getMessage(),
                        0L
                );
                metricsService.recordTaskFailure();
            } else if (result.isSuccess()) {
                coordinatorClient.reportTaskCompletion(
                        coordinatorClient.getWorkerId(),
                        taskId,
                        result.getResultData(),
                        result.getExecutionTimeMs()
                );
                metricsService.recordTaskCompletion(result.getExecutionTimeMs());
            } else {
                coordinatorClient.reportTaskFailure(
                        coordinatorClient.getWorkerId(),
                        taskId,
                        result.getErrorMessage(),
                        result.getExecutionTimeMs()
                );
                metricsService.recordTaskFailure();
            }
        });

        // Track the running task
        runningTasks.put(taskId, future);
        return true;
    }

    /**
     * Cancels a running task by interrupting its thread.
     *
     * @param taskId the ID of the task to cancel
     * @return true if the task was found and cancellation was requested
     */
    public boolean cancelTask(String taskId) {
        Future<?> future = runningTasks.remove(taskId);
        if (future != null) {
            boolean cancelled = future.cancel(true);
            if (cancelled) {
                runningTaskCount.decrementAndGet();
                log.info("Task cancelled: taskId={}", taskId);
            } else {
                log.warn("Task cancellation requested but task may have already completed: taskId={}", taskId);
            }
            return cancelled;
        }
        log.warn("Task not found for cancellation: taskId={}", taskId);
        return false;
    }

    /**
     * Returns the current number of running tasks.
     */
    public int getRunningTaskCount() {
        return runningTaskCount.get();
    }

    /**
     * Returns the set of currently running task IDs.
     */
    public Set<String> getRunningTaskIds() {
        return Collections.unmodifiableSet(runningTasks.keySet());
    }

    /**
     * Checks if a specific task is currently running.
     */
    public boolean isTaskRunning(String taskId) {
        return runningTasks.containsKey(taskId);
    }

    /**
     * Returns the available capacity (how many more tasks can be accepted).
     */
    public int getAvailableSlots() {
        return Math.max(0, taskExecutorPool.getMaximumPoolSize() - runningTaskCount.get());
    }
}
