package com.taskengine.common.model;

/**
 * Represents the health states of a Worker node.
 *
 * <p>State transitions (based on heartbeat monitoring):
 * <pre>
 *   ACTIVE → UNHEALTHY (no heartbeat for 10s)
 *          → DEAD (no heartbeat for 15s)
 *   UNHEALTHY → ACTIVE (heartbeat received)
 *             → DEAD (no heartbeat for 15s)
 *   DEAD → (removed / tasks reassigned)
 * </pre>
 */
public enum WorkerStatus {
    ACTIVE,
    UNHEALTHY,
    DEAD;

    /**
     * Checks if the worker can accept new tasks.
     */
    public boolean canAcceptTasks() {
        return this == ACTIVE;
    }

    /**
     * Checks if the worker's tasks should be reassigned.
     */
    public boolean shouldReassignTasks() {
        return this == DEAD;
    }
}
