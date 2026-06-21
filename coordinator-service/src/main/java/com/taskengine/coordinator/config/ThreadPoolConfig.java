package com.taskengine.coordinator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Configuration for the dedicated thread pools used by the coordinator.
 *
 * <p>Provides separate {@link ScheduledExecutorService} beans for:
 * <ul>
 *     <li><b>Task scheduling</b> — periodically polls for pending tasks and assigns them</li>
 *     <li><b>Heartbeat monitoring</b> — periodically checks worker liveness</li>
 * </ul>
 *
 * <p>Using dedicated pools prevents starvation: a blocked task assignment cannot
 * delay heartbeat checks, and vice versa.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Configuration
public class ThreadPoolConfig {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolConfig.class);

    private ScheduledExecutorService taskSchedulerExecutor;
    private ScheduledExecutorService heartbeatMonitorExecutor;

    /**
     * Creates a 2-thread scheduled executor for task scheduling operations.
     *
     * <p>Two threads allow one scheduling cycle to overlap with an async task
     * assignment without blocking the next scheduling tick.
     *
     * @return the task scheduler executor service
     */
    @Bean(name = "taskSchedulerExecutor")
    public ScheduledExecutorService taskSchedulerExecutor() {
        this.taskSchedulerExecutor = Executors.newScheduledThreadPool(2, r -> {
            Thread thread = new Thread(r, "task-scheduler");
            thread.setDaemon(true);
            return thread;
        });
        log.info("Created task scheduler executor with 2 threads");
        return this.taskSchedulerExecutor;
    }

    /**
     * Creates a single-thread scheduled executor for heartbeat monitoring.
     *
     * <p>A single thread is sufficient because heartbeat checks are fast
     * in-memory operations; the actual fault recovery work is delegated
     * to the {@code FaultRecoveryService} asynchronously.
     *
     * @return the heartbeat monitor executor service
     */
    @Bean(name = "heartbeatMonitorExecutor")
    public ScheduledExecutorService heartbeatMonitorExecutor() {
        this.heartbeatMonitorExecutor = Executors.newScheduledThreadPool(1, r -> {
            Thread thread = new Thread(r, "heartbeat-monitor");
            thread.setDaemon(true);
            return thread;
        });
        log.info("Created heartbeat monitor executor with 1 thread");
        return this.heartbeatMonitorExecutor;
    }

    /**
     * Gracefully shuts down both executor services on application context close.
     */
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down coordinator thread pools");
        shutdownExecutor(taskSchedulerExecutor, "task-scheduler");
        shutdownExecutor(heartbeatMonitorExecutor, "heartbeat-monitor");
    }

    private void shutdownExecutor(ScheduledExecutorService executor, String name) {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.warn("{} executor did not terminate in time, forcing shutdown", name);
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while shutting down {} executor", name, e);
                executor.shutdownNow();
            }
        }
    }
}
