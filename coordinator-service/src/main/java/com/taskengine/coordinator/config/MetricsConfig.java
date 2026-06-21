package com.taskengine.coordinator.config;

import com.taskengine.common.model.TaskStatus;
import com.taskengine.common.model.WorkerStatus;
import com.taskengine.coordinator.persistence.repository.TaskRepository;
import com.taskengine.coordinator.persistence.repository.WorkerRepository;
import com.taskengine.coordinator.registry.WorkerRegistryService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Micrometer metrics exposed to Prometheus.
 *
 * <p>Registers gauges, counters, and timers that provide operational visibility
 * into the coordinator's state:
 * <ul>
 *     <li><b>Gauges</b> — active workers, queued tasks, running tasks</li>
 *     <li><b>Counters</b> — completed tasks, failed tasks, retries</li>
 *     <li><b>Timers</b> — task execution time distribution</li>
 * </ul>
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Configuration
public class MetricsConfig {

    private static final Logger log = LoggerFactory.getLogger(MetricsConfig.class);

    /**
     * Registers all coordinator metrics with the Micrometer registry.
     *
     * @param meterRegistry   the meter registry (auto-configured by Spring Boot)
     * @param workerRepository the worker repository for gauge queries
     * @param taskRepository   the task repository for gauge queries
     * @param registryService  the worker registry for live worker count
     * @return a marker bean to ensure this configuration is loaded
     */
    @Bean
    public MetricsRegistrar metricsRegistrar(
            MeterRegistry meterRegistry,
            WorkerRepository workerRepository,
            TaskRepository taskRepository,
            WorkerRegistryService registryService) {

        // Gauge: number of active workers in the registry
        meterRegistry.gauge("coordinator.workers.active",
                registryService,
                reg -> reg.getActiveWorkerCount());

        // Gauge: number of tasks in PENDING or RETRYING state (queued for scheduling)
        meterRegistry.gauge("coordinator.tasks.queued",
                taskRepository,
                repo -> repo.countByStatus(TaskStatus.PENDING) + repo.countByStatus(TaskStatus.RETRYING));

        // Gauge: number of tasks currently RUNNING or ASSIGNED
        meterRegistry.gauge("coordinator.tasks.running",
                taskRepository,
                repo -> repo.countByStatus(TaskStatus.RUNNING) + repo.countByStatus(TaskStatus.ASSIGNED));

        log.info("Registered coordinator Micrometer gauges: active_workers, queued_tasks, running_tasks");

        return new MetricsRegistrar(meterRegistry);
    }

    /**
     * Holder for counter and timer beans that other services can inject.
     */
    public static class MetricsRegistrar {

        private final Counter completedTasksCounter;
        private final Counter failedTasksCounter;
        private final Counter retryCounter;
        private final Timer taskExecutionTimer;

        /**
         * Creates and registers counters and timers.
         *
         * @param meterRegistry the Micrometer meter registry
         */
        public MetricsRegistrar(MeterRegistry meterRegistry) {
            this.completedTasksCounter = Counter.builder("coordinator.tasks.completed.total")
                    .description("Total number of tasks completed successfully")
                    .register(meterRegistry);

            this.failedTasksCounter = Counter.builder("coordinator.tasks.failed.total")
                    .description("Total number of tasks that failed permanently")
                    .register(meterRegistry);

            this.retryCounter = Counter.builder("coordinator.tasks.retries.total")
                    .description("Total number of task retry attempts")
                    .register(meterRegistry);

            this.taskExecutionTimer = Timer.builder("coordinator.tasks.execution.time")
                    .description("Time taken to execute individual tasks")
                    .publishPercentileHistogram()
                    .register(meterRegistry);
        }

        /** Increments the completed tasks counter. */
        public void recordTaskCompleted() {
            completedTasksCounter.increment();
        }

        /** Increments the failed tasks counter. */
        public void recordTaskFailed() {
            failedTasksCounter.increment();
        }

        /** Increments the retry counter. */
        public void recordTaskRetry() {
            retryCounter.increment();
        }

        /**
         * Records a task execution duration.
         *
         * @param executionTimeMs the execution time in milliseconds
         */
        public void recordTaskExecutionTime(long executionTimeMs) {
            taskExecutionTimer.record(java.time.Duration.ofMillis(executionTimeMs));
        }

        public Counter getCompletedTasksCounter() {
            return completedTasksCounter;
        }

        public Counter getFailedTasksCounter() {
            return failedTasksCounter;
        }

        public Counter getRetryCounter() {
            return retryCounter;
        }

        public Timer getTaskExecutionTimer() {
            return taskExecutionTimer;
        }
    }
}
