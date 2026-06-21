package com.taskengine.coordinator.persistence.entity;

import com.taskengine.common.model.JobStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing a job in the distributed task execution engine.
 *
 * <p>A job is a top-level unit of work submitted by a client. It is decomposed into
 * one or more {@link TaskEntity tasks} by a job splitter. The coordinator tracks
 * job progress by aggregating the status of its constituent tasks.
 *
 * <p>Job lifecycle: SUBMITTED → RUNNING → COMPLETED | FAILED | CANCELLED
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Entity
@Table(name = "jobs")
public class JobEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private JobStatus status;

    @Column(name = "input_data", columnDefinition = "TEXT")
    private String inputData;

    @Column(name = "total_tasks", nullable = false)
    private int totalTasks;

    @Column(name = "completed_tasks", nullable = false)
    private int completedTasks;

    @Column(name = "failed_tasks", nullable = false)
    private int failedTasks;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Default constructor required by JPA.
     */
    public JobEntity() {
        // JPA requires a no-arg constructor
    }

    /**
     * Sets default timestamps before initial persistence.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = JobStatus.SUBMITTED;
        }
    }

    /**
     * Updates the {@code updated_at} timestamp before every update.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ---- Getters and Setters ----

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    public int getFailedTasks() {
        return failedTasks;
    }

    public void setFailedTasks(int failedTasks) {
        this.failedTasks = failedTasks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public String toString() {
        return "JobEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", status=" + status +
                ", totalTasks=" + totalTasks +
                ", completedTasks=" + completedTasks +
                ", failedTasks=" + failedTasks +
                '}';
    }
}
