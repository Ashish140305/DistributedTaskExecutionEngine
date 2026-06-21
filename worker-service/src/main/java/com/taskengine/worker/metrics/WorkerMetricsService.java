package com.taskengine.worker.metrics;

import com.taskengine.worker.engine.TaskExecutionEngine;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Worker metrics service for Prometheus/Grafana observability.
 *
 * <p>Registers and updates the following metrics:
 * <ul>
 *   <li>{@code taskengine.worker.running_tasks} — gauge of currently running tasks</li>
 *   <li>{@code taskengine.worker.tasks.completed} — counter of completed tasks</li>
 *   <li>{@code taskengine.worker.tasks.failed} — counter of failed tasks</li>
 *   <li>{@code taskengine.worker.task.execution.time} — timer of task execution duration</li>
 * </ul>
 */
@Component
public class WorkerMetricsService {

    private final Counter completedTasksCounter;
    private final Counter failedTasksCounter;
    private final Timer executionTimer;

    public WorkerMetricsService(MeterRegistry meterRegistry, @Lazy TaskExecutionEngine executionEngine) {
        // Gauge: running tasks (sampled from execution engine)
        meterRegistry.gauge("taskengine.worker.running_tasks", executionEngine, TaskExecutionEngine::getRunningTaskCount);

        // Counter: completed tasks
        this.completedTasksCounter = Counter.builder("taskengine.worker.tasks.completed")
                .description("Total number of tasks completed by this worker")
                .register(meterRegistry);

        // Counter: failed tasks
        this.failedTasksCounter = Counter.builder("taskengine.worker.tasks.failed")
                .description("Total number of tasks failed on this worker")
                .register(meterRegistry);

        // Timer: task execution time
        this.executionTimer = Timer.builder("taskengine.worker.task.execution.time")
                .description("Task execution time distribution")
                .register(meterRegistry);
    }

    /**
     * Records a successful task completion with its execution time.
     *
     * @param executionTimeMs the task execution time in milliseconds
     */
    public void recordTaskCompletion(long executionTimeMs) {
        completedTasksCounter.increment();
        executionTimer.record(executionTimeMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Records a task failure.
     */
    public void recordTaskFailure() {
        failedTasksCounter.increment();
    }
}
