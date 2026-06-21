package com.taskengine.common.model;

/**
 * Represents the lifecycle states of a Task.
 *
 * <p>State transitions:
 * <pre>
 *   PENDING → ASSIGNED → RUNNING → COMPLETED
 *                                → FAILED → RETRYING → ASSIGNED
 *                                         → FAILED (max retries exceeded)
 *           → CANCELLED
 * </pre>
 */
public enum TaskStatus {
    PENDING,
    ASSIGNED,
    RUNNING,
    COMPLETED,
    FAILED,
    RETRYING,
    CANCELLED;

    /**
     * Checks if this status represents a terminal state.
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    /**
     * Checks if the task is still in progress or awaiting execution.
     */
    public boolean isActive() {
        return this == PENDING || this == ASSIGNED || this == RUNNING || this == RETRYING;
    }

    /**
     * Checks if the task can be retried.
     */
    public boolean isRetryable() {
        return this == FAILED || this == RETRYING;
    }
}
