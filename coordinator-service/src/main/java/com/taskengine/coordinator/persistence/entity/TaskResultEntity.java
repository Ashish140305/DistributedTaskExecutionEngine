package com.taskengine.coordinator.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing the result of a single task execution attempt.
 *
 * <p>Each time a task is executed (including retries), a result record is created
 * to capture the outcome, execution time, and any error details. This provides
 * a full audit trail of all execution attempts for diagnostics and analysis.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Entity
@Table(name = "task_results")
public class TaskResultEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private TaskEntity task;

    @Column(name = "result_data", columnDefinition = "TEXT")
    private String resultData;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(name = "attempt_number", nullable = false)
    private int attemptNumber;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Default constructor required by JPA.
     */
    public TaskResultEntity() {
        // JPA requires a no-arg constructor
    }

    /**
     * Sets the creation timestamp before initial persistence.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ---- Getters and Setters ----

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TaskEntity getTask() {
        return task;
    }

    public void setTask(TaskEntity task) {
        this.task = task;
    }

    public String getResultData() {
        return resultData;
    }

    public void setResultData(String resultData) {
        this.resultData = resultData;
    }

    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(int attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "TaskResultEntity{" +
                "id=" + id +
                ", taskId=" + (task != null ? task.getId() : null) +
                ", status='" + status + '\'' +
                ", attemptNumber=" + attemptNumber +
                ", executionTimeMs=" + executionTimeMs +
                '}';
    }
}
