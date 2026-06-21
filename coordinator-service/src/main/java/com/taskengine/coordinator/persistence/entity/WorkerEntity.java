package com.taskengine.coordinator.persistence.entity;

import com.taskengine.common.model.WorkerStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing a registered worker node.
 *
 * <p>Workers are computational nodes that execute tasks. They register with the
 * coordinator on startup and maintain their liveness through periodic heartbeats.
 * The coordinator monitors heartbeats to detect failures and trigger fault recovery.
 *
 * <p>Worker lifecycle: ACTIVE → UNHEALTHY → DEAD (on missed heartbeats)
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Entity
@Table(name = "workers")
public class WorkerEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "hostname", nullable = false)
    private String hostname;

    @Column(name = "port", nullable = false)
    private int port;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private WorkerStatus status;

    @Column(name = "last_heartbeat", nullable = false)
    private LocalDateTime lastHeartbeat;

    @Column(name = "running_tasks", nullable = false)
    private int runningTasks;

    @Column(name = "max_concurrent_tasks", nullable = false)
    private int maxConcurrentTasks;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Default constructor required by JPA.
     */
    public WorkerEntity() {
        // JPA requires a no-arg constructor
    }

    /**
     * Sets default timestamps and status before initial persistence.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.lastHeartbeat = now;
        if (this.status == null) {
            this.status = WorkerStatus.ACTIVE;
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

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public WorkerStatus getStatus() {
        return status;
    }

    public void setStatus(WorkerStatus status) {
        this.status = status;
    }

    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(LocalDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public int getRunningTasks() {
        return runningTasks;
    }

    public void setRunningTasks(int runningTasks) {
        this.runningTasks = runningTasks;
    }

    public int getMaxConcurrentTasks() {
        return maxConcurrentTasks;
    }

    public void setMaxConcurrentTasks(int maxConcurrentTasks) {
        this.maxConcurrentTasks = maxConcurrentTasks;
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

    @Override
    public String toString() {
        return "WorkerEntity{" +
                "id=" + id +
                ", hostname='" + hostname + '\'' +
                ", port=" + port +
                ", status=" + status +
                ", runningTasks=" + runningTasks +
                ", maxConcurrentTasks=" + maxConcurrentTasks +
                '}';
    }
}
