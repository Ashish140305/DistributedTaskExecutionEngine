package com.taskengine.coordinator.registry;

import com.taskengine.common.model.WorkerStatus;
import com.taskengine.common.scheduler.SchedulingStrategy.WorkerSnapshot;
import com.taskengine.coordinator.persistence.entity.WorkerEntity;
import com.taskengine.proto.WorkerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * In-memory registry of active worker connections.
 *
 * <p>Maintains a {@link ConcurrentHashMap} of {@link WorkerConnection} records
 * indexed by worker ID. Each connection holds the worker's metadata alongside
 * a lazily-created gRPC {@link ManagedChannel} and {@link WorkerServiceGrpc.WorkerServiceBlockingStub}.
 *
 * <p>The registry is the single source of truth for:
 * <ul>
 *     <li>Which workers are currently reachable</li>
 *     <li>The gRPC stubs needed to communicate with each worker</li>
 *     <li>Worker capacity snapshots for the scheduler</li>
 * </ul>
 *
 * <p>Thread-safety is guaranteed by the underlying ConcurrentHashMap. Individual
 * WorkerConnection records are effectively immutable once published, and updates
 * replace the entire record atomically.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Service
public class WorkerRegistryService {

    private static final Logger log = LoggerFactory.getLogger(WorkerRegistryService.class);

    /**
     * Holds the gRPC connection artifacts and metadata for a single worker.
     *
     * @param workerEntity       the persisted worker entity
     * @param channel            the gRPC managed channel to the worker
     * @param stub               the blocking stub for making RPC calls
     */
    public record WorkerConnection(
            WorkerEntity workerEntity,
            ManagedChannel channel,
            WorkerServiceGrpc.WorkerServiceBlockingStub stub
    ) {}

    private final ConcurrentHashMap<String, WorkerConnection> workers = new ConcurrentHashMap<>();

    /**
     * Registers a worker in the in-memory registry, creating a gRPC channel and stub.
     *
     * @param workerEntity the persisted worker entity containing hostname and port
     */
    public void registerWorker(WorkerEntity workerEntity) {
        String workerId = workerEntity.getId().toString();
        String hostname = workerEntity.getHostname();
        int port = workerEntity.getPort();

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(hostname, port)
                .usePlaintext()
                .build();

        WorkerServiceGrpc.WorkerServiceBlockingStub stub = WorkerServiceGrpc.newBlockingStub(channel);

        WorkerConnection connection = new WorkerConnection(workerEntity, channel, stub);
        workers.put(workerId, connection);

        log.info("Registered worker '{}' at {}:{} in the in-memory registry", workerId, hostname, port);
    }

    /**
     * Removes a worker from the registry and shuts down its gRPC channel.
     *
     * @param workerId the worker's UUID string
     */
    public void removeWorker(String workerId) {
        WorkerConnection connection = workers.remove(workerId);
        if (connection != null) {
            shutdownChannel(connection.channel(), workerId);
            log.info("Removed worker '{}' from the in-memory registry", workerId);
        } else {
            log.warn("Attempted to remove unknown worker '{}' from registry", workerId);
        }
    }

    /**
     * Returns all connections for workers that are currently marked ACTIVE.
     *
     * @return list of active worker connections
     */
    public List<WorkerConnection> getActiveWorkers() {
        return workers.values().stream()
                .filter(c -> c.workerEntity().getStatus() == WorkerStatus.ACTIVE)
                .toList();
    }

    /**
     * Retrieves the gRPC blocking stub for the specified worker.
     *
     * @param workerId the worker's UUID string
     * @return an Optional containing the stub if the worker is registered
     */
    public Optional<WorkerServiceGrpc.WorkerServiceBlockingStub> getWorkerStub(String workerId) {
        WorkerConnection connection = workers.get(workerId);
        if (connection != null) {
            return Optional.of(connection.stub());
        }
        return Optional.empty();
    }

    /**
     * Retrieves the worker connection for the specified worker.
     *
     * @param workerId the worker's UUID string
     * @return an Optional containing the connection if found
     */
    public Optional<WorkerConnection> getWorkerConnection(String workerId) {
        return Optional.ofNullable(workers.get(workerId));
    }

    /**
     * Updates the heartbeat timestamp and running task count for a worker in the registry.
     *
     * @param workerId     the worker's UUID string
     * @param runningTasks the current number of running tasks on the worker
     */
    public void updateHeartbeat(String workerId, int runningTasks) {
        WorkerConnection connection = workers.get(workerId);
        if (connection != null) {
            WorkerEntity entity = connection.workerEntity();
            entity.setLastHeartbeat(LocalDateTime.now());
            entity.setRunningTasks(runningTasks);
            entity.setStatus(WorkerStatus.ACTIVE);
            log.debug("Updated heartbeat for worker '{}'. Running tasks: {}", workerId, runningTasks);
        } else {
            log.warn("Heartbeat received for unknown worker '{}'", workerId);
        }
    }

    /**
     * Updates the status of a worker in the registry.
     *
     * @param workerId  the worker's UUID string
     * @param newStatus the new worker status
     */
    public void updateWorkerStatus(String workerId, WorkerStatus newStatus) {
        WorkerConnection connection = workers.get(workerId);
        if (connection != null) {
            WorkerStatus oldStatus = connection.workerEntity().getStatus();
            connection.workerEntity().setStatus(newStatus);
            log.info("Worker '{}' status changed: {} → {}", workerId, oldStatus, newStatus);
        }
    }

    /**
     * Creates worker snapshots for all active workers, suitable for scheduling decisions.
     *
     * @return list of worker snapshots with capacity information
     */
    public List<WorkerSnapshot> getWorkerSnapshots() {
        return workers.values().stream()
                .filter(c -> c.workerEntity().getStatus() == WorkerStatus.ACTIVE)
                .map(c -> {
                    WorkerEntity we = c.workerEntity();
                    return new WorkerSnapshot(
                            we.getId().toString(),
                            we.getRunningTasks(),
                            we.getMaxConcurrentTasks()
                    );
                })
                .toList();
    }

    /**
     * Returns the total number of workers currently in the registry (all statuses).
     *
     * @return worker count
     */
    public int getWorkerCount() {
        return workers.size();
    }

    /**
     * Returns the number of active workers in the registry.
     *
     * @return count of active workers
     */
    public long getActiveWorkerCount() {
        return workers.values().stream()
                .filter(c -> c.workerEntity().getStatus() == WorkerStatus.ACTIVE)
                .count();
    }

    /**
     * Returns all worker connections (regardless of status).
     *
     * @return an unmodifiable view of all worker connections
     */
    public Map<String, WorkerConnection> getAllWorkers() {
        return Map.copyOf(workers);
    }

    /**
     * Gracefully shuts down a gRPC channel.
     */
    private void shutdownChannel(ManagedChannel channel, String workerId) {
        try {
            channel.shutdown();
            if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("Channel for worker '{}' did not terminate in time, forcing shutdown", workerId);
                channel.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while shutting down channel for worker '{}'", workerId, e);
            channel.shutdownNow();
        }
    }
}
