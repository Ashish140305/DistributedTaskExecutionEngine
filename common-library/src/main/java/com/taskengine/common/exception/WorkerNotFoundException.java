package com.taskengine.common.exception;

/**
 * Thrown when a requested worker cannot be found.
 */
public class WorkerNotFoundException extends RuntimeException {

    private final String workerId;

    public WorkerNotFoundException(String workerId) {
        super("Worker not found: " + workerId);
        this.workerId = workerId;
    }

    public String getWorkerId() {
        return workerId;
    }
}
