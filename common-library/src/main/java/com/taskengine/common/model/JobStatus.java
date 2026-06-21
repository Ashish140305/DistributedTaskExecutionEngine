package com.taskengine.common.model;

/**
 * Represents the lifecycle states of a Job.
 *
 * <p>State transitions:
 * <pre>
 *   SUBMITTED → RUNNING → COMPLETED
 *                       → FAILED
 *              → CANCELLED
 * </pre>
 */
public enum JobStatus {
    SUBMITTED,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED;

    /**
     * Checks if this status represents a terminal state (no further transitions possible).
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    /**
     * Checks if the job is still in progress.
     */
    public boolean isActive() {
        return this == SUBMITTED || this == RUNNING;
    }
}
