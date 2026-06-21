package com.taskengine.common.model;

import java.util.Map;

/**
 * Immutable context object passed to a {@link com.taskengine.common.executor.TaskExecutor}
 * when executing a task. Contains all information the executor needs.
 *
 * <p>Uses the Builder pattern for clean, readable construction.
 */
public class TaskContext {

    private final String taskId;
    private final String jobId;
    private final String taskType;
    private final String inputData;
    private final Map<String, String> parameters;
    private final int retryCount;
    private final int maxRetries;

    private TaskContext(Builder builder) {
        this.taskId = builder.taskId;
        this.jobId = builder.jobId;
        this.taskType = builder.taskType;
        this.inputData = builder.inputData;
        this.parameters = builder.parameters != null ? Map.copyOf(builder.parameters) : Map.of();
        this.retryCount = builder.retryCount;
        this.maxRetries = builder.maxRetries;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getJobId() {
        return jobId;
    }

    public String getTaskType() {
        return taskType;
    }

    public String getInputData() {
        return inputData;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public boolean isRetry() {
        return retryCount > 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "TaskContext{" +
                "taskId='" + taskId + '\'' +
                ", jobId='" + jobId + '\'' +
                ", taskType='" + taskType + '\'' +
                ", retryCount=" + retryCount +
                '}';
    }

    public static class Builder {
        private String taskId;
        private String jobId;
        private String taskType;
        private String inputData;
        private Map<String, String> parameters;
        private int retryCount;
        private int maxRetries = 3;

        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder jobId(String jobId) {
            this.jobId = jobId;
            return this;
        }

        public Builder taskType(String taskType) {
            this.taskType = taskType;
            return this;
        }

        public Builder inputData(String inputData) {
            this.inputData = inputData;
            return this;
        }

        public Builder parameters(Map<String, String> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder retryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public TaskContext build() {
            if (taskId == null || taskId.isBlank()) {
                throw new IllegalArgumentException("taskId is required");
            }
            if (jobId == null || jobId.isBlank()) {
                throw new IllegalArgumentException("jobId is required");
            }
            if (taskType == null || taskType.isBlank()) {
                throw new IllegalArgumentException("taskType is required");
            }
            return new TaskContext(this);
        }
    }
}
