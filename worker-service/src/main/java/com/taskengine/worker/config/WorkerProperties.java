package com.taskengine.worker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Type-safe configuration properties for the worker node, bound to
 * the {@code worker.*} namespace in {@code application.yml}.
 *
 * <p>Defaults:
 * <ul>
 *   <li>{@code maxConcurrentTasks} = 4</li>
 *   <li>{@code heartbeatIntervalMs} = 5000</li>
 * </ul>
 */
@ConfigurationProperties(prefix = "worker")
public class WorkerProperties {

    /**
     * Maximum number of tasks this worker can execute concurrently.
     * Maps directly to the core/max size of the task thread-pool.
     */
    private int maxConcurrentTasks = 4;

    /**
     * Interval in milliseconds between heartbeat messages sent
     * to the coordinator service.
     */
    private long heartbeatIntervalMs = 5000;

    public int getMaxConcurrentTasks() {
        return maxConcurrentTasks;
    }

    public void setMaxConcurrentTasks(int maxConcurrentTasks) {
        this.maxConcurrentTasks = maxConcurrentTasks;
    }

    public long getHeartbeatIntervalMs() {
        return heartbeatIntervalMs;
    }

    public void setHeartbeatIntervalMs(long heartbeatIntervalMs) {
        this.heartbeatIntervalMs = heartbeatIntervalMs;
    }

    @Override
    public String toString() {
        return "WorkerProperties{" +
                "maxConcurrentTasks=" + maxConcurrentTasks +
                ", heartbeatIntervalMs=" + heartbeatIntervalMs +
                '}';
    }
}
