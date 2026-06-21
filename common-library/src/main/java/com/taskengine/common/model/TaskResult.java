package com.taskengine.common.model;

/**
 * Immutable result of a task execution, produced by a
 * {@link com.taskengine.common.executor.TaskExecutor}.
 *
 * <p>Uses the Builder pattern. Captures success/failure outcome,
 * result data, error info, and execution timing.
 */
public class TaskResult {

    private final String taskId;
    private final boolean success;
    private final String resultData;
    private final String errorMessage;
    private final long executionTimeMs;
    private final int attemptNumber;

    private TaskResult(Builder builder) {
        this.taskId = builder.taskId;
        this.success = builder.success;
        this.resultData = builder.resultData;
        this.errorMessage = builder.errorMessage;
        this.executionTimeMs = builder.executionTimeMs;
        this.attemptNumber = builder.attemptNumber;
    }

    public String getTaskId() {
        return taskId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getResultData() {
        return resultData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    /**
     * Factory method for a successful result.
     */
    public static TaskResult success(String taskId, String resultData, long executionTimeMs, int attempt) {
        return builder()
                .taskId(taskId)
                .success(true)
                .resultData(resultData)
                .executionTimeMs(executionTimeMs)
                .attemptNumber(attempt)
                .build();
    }

    /**
     * Factory method for a failed result.
     */
    public static TaskResult failure(String taskId, String errorMessage, long executionTimeMs, int attempt) {
        return builder()
                .taskId(taskId)
                .success(false)
                .errorMessage(errorMessage)
                .executionTimeMs(executionTimeMs)
                .attemptNumber(attempt)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "TaskResult{" +
                "taskId='" + taskId + '\'' +
                ", success=" + success +
                ", executionTimeMs=" + executionTimeMs +
                ", attemptNumber=" + attemptNumber +
                '}';
    }

    public static class Builder {
        private String taskId;
        private boolean success;
        private String resultData;
        private String errorMessage;
        private long executionTimeMs;
        private int attemptNumber = 1;

        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder resultData(String resultData) {
            this.resultData = resultData;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder executionTimeMs(long executionTimeMs) {
            this.executionTimeMs = executionTimeMs;
            return this;
        }

        public Builder attemptNumber(int attemptNumber) {
            this.attemptNumber = attemptNumber;
            return this;
        }

        public TaskResult build() {
            if (taskId == null || taskId.isBlank()) {
                throw new IllegalArgumentException("taskId is required");
            }
            return new TaskResult(this);
        }
    }
}
