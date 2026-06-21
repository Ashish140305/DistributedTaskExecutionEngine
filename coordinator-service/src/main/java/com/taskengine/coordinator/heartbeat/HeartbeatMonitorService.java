package com.taskengine.coordinator.heartbeat;

import com.taskengine.common.model.WorkerStatus;
import com.taskengine.coordinator.fault.FaultRecoveryService;
import com.taskengine.coordinator.persistence.entity.WorkerEntity;
import com.taskengine.coordinator.persistence.repository.WorkerRepository;
import com.taskengine.coordinator.registry.WorkerRegistryService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Heartbeat monitor that periodically checks worker liveness.
 *
 * <p>Runs on a dedicated {@link ScheduledExecutorService} every 5 seconds and
 * inspects each worker's last heartbeat timestamp:
 * <ul>
 *     <li><b>&gt; 10s since last heartbeat</b> → marks worker as UNHEALTHY</li>
 *     <li><b>&gt; 15s since last heartbeat</b> → marks worker as DEAD and triggers fault recovery</li>
 * </ul>
 *
 * <p>All state transitions are logged at INFO level for operational visibility.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Service
public class HeartbeatMonitorService {

    private static final Logger log = LoggerFactory.getLogger(HeartbeatMonitorService.class);

    private static final long CHECK_INTERVAL_SECONDS = 5;
    private static final Duration UNHEALTHY_THRESHOLD = Duration.ofSeconds(10);
    private static final Duration DEAD_THRESHOLD = Duration.ofSeconds(15);

    private final ScheduledExecutorService heartbeatExecutor;
    private final WorkerRepository workerRepository;
    private final WorkerRegistryService workerRegistry;
    private final FaultRecoveryService faultRecoveryService;

    /**
     * Constructs the heartbeat monitor.
     *
     * @param heartbeatExecutor    the scheduled executor for periodic checks
     * @param workerRepository     the worker repository for DB reads/writes
     * @param workerRegistry       the in-memory worker registry
     * @param faultRecoveryService the fault recovery service for dead worker handling
     */
    public HeartbeatMonitorService(
            @Qualifier("heartbeatMonitorExecutor") ScheduledExecutorService heartbeatExecutor,
            WorkerRepository workerRepository,
            WorkerRegistryService workerRegistry,
            FaultRecoveryService faultRecoveryService) {
        this.heartbeatExecutor = heartbeatExecutor;
        this.workerRepository = workerRepository;
        this.workerRegistry = workerRegistry;
        this.faultRecoveryService = faultRecoveryService;
    }

    /**
     * Starts the periodic heartbeat check loop after bean initialization.
     */
    @PostConstruct
    public void start() {
        log.info("Starting heartbeat monitor with {}s check interval. " +
                        "Unhealthy threshold: {}s, Dead threshold: {}s",
                CHECK_INTERVAL_SECONDS,
                UNHEALTHY_THRESHOLD.toSeconds(),
                DEAD_THRESHOLD.toSeconds());

        heartbeatExecutor.scheduleAtFixedRate(
                this::checkHeartbeats,
                CHECK_INTERVAL_SECONDS,
                CHECK_INTERVAL_SECONDS,
                TimeUnit.SECONDS
        );
    }

    /**
     * Single heartbeat check cycle.
     *
     * <p>Iterates over all workers with an ACTIVE or UNHEALTHY status and
     * evaluates the elapsed time since their last heartbeat.
     *
     * <p>Exceptions are caught to prevent the scheduled task from being cancelled.
     */
    void checkHeartbeats() {
        try {
            LocalDateTime now = LocalDateTime.now();

            // Check ACTIVE workers that might have become UNHEALTHY
            List<WorkerEntity> activeWorkers = workerRepository.findByStatus(WorkerStatus.ACTIVE);
            for (WorkerEntity worker : activeWorkers) {
                Duration elapsed = Duration.between(worker.getLastHeartbeat(), now);

                if (elapsed.compareTo(DEAD_THRESHOLD) > 0) {
                    markDead(worker, elapsed);
                } else if (elapsed.compareTo(UNHEALTHY_THRESHOLD) > 0) {
                    markUnhealthy(worker, elapsed);
                }
            }

            // Check UNHEALTHY workers that might have progressed to DEAD
            List<WorkerEntity> unhealthyWorkers = workerRepository.findByStatus(WorkerStatus.UNHEALTHY);
            for (WorkerEntity worker : unhealthyWorkers) {
                Duration elapsed = Duration.between(worker.getLastHeartbeat(), now);

                if (elapsed.compareTo(DEAD_THRESHOLD) > 0) {
                    markDead(worker, elapsed);
                }
            }

        } catch (Exception e) {
            log.error("Error during heartbeat check cycle", e);
        }
    }

    /**
     * Transitions a worker to UNHEALTHY state.
     *
     * @param worker  the worker entity
     * @param elapsed time since last heartbeat
     */
    private void markUnhealthy(WorkerEntity worker, Duration elapsed) {
        String workerId = worker.getId().toString();
        WorkerStatus oldStatus = worker.getStatus();

        worker.setStatus(WorkerStatus.UNHEALTHY);
        workerRepository.save(worker);

        workerRegistry.updateWorkerStatus(workerId, WorkerStatus.UNHEALTHY);

        log.warn("Worker '{}' ({}:{}) marked UNHEALTHY. Last heartbeat: {}s ago. Previous status: {}",
                workerId, worker.getHostname(), worker.getPort(),
                elapsed.toSeconds(), oldStatus);
    }

    /**
     * Transitions a worker to DEAD state and triggers fault recovery.
     *
     * @param worker  the worker entity
     * @param elapsed time since last heartbeat
     */
    private void markDead(WorkerEntity worker, Duration elapsed) {
        String workerId = worker.getId().toString();
        WorkerStatus oldStatus = worker.getStatus();

        worker.setStatus(WorkerStatus.DEAD);
        workerRepository.save(worker);

        workerRegistry.updateWorkerStatus(workerId, WorkerStatus.DEAD);

        log.error("Worker '{}' ({}:{}) marked DEAD. Last heartbeat: {}s ago. Previous status: {}. " +
                        "Triggering fault recovery.",
                workerId, worker.getHostname(), worker.getPort(),
                elapsed.toSeconds(), oldStatus);

        // Trigger fault recovery for the dead worker
        try {
            faultRecoveryService.handleDeadWorker(workerId);
        } catch (Exception e) {
            log.error("Failed to perform fault recovery for dead worker '{}'", workerId, e);
        }
    }
}
