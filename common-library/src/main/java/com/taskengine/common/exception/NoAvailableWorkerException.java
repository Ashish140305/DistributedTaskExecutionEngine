package com.taskengine.common.exception;

/**
 * Thrown when no workers are available to accept task assignments.
 */
public class NoAvailableWorkerException extends RuntimeException {

    public NoAvailableWorkerException() {
        super("No available workers to accept task assignments");
    }

    public NoAvailableWorkerException(String message) {
        super(message);
    }
}
