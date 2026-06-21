package com.taskengine.common.exception;

/**
 * Thrown when a task execution fails within a worker.
 */
public class TaskExecutionException extends RuntimeException {

    private final String taskId;

    public TaskExecutionException(String taskId, String message) {
        super("Task execution failed [" + taskId + "]: " + message);
        this.taskId = taskId;
    }

    public TaskExecutionException(String taskId, String message, Throwable cause) {
        super("Task execution failed [" + taskId + "]: " + message, cause);
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}
