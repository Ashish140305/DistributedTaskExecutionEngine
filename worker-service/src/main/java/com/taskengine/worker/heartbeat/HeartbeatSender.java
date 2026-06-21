package com.taskengine.worker.heartbeat;

import com.taskengine.worker.engine.TaskExecutionEngine;
import com.taskengine.worker.grpc.CoordinatorGrpcClient;
import com.taskengine.worker.config.WorkerProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Sends periodic heartbeats to the Coordinator service.
 *
 * <p>Implements {@link SmartLifecycle} to start after application context
 * is ready and stop cleanly on shutdown. Heartbeats are sent at a fixed
 * rate (default 5 seconds) using a {@link ScheduledExecutorService}.
 *
 * <p>Each heartbeat includes:
 * <ul>
 *   <li>Worker ID</li>
 *   <li>Number of currently running tasks</li>
 *   <li>Number of available task slots</li>
 * </ul>
 *
 * <p>Failed heartbeats are logged but do not crash the worker.
 */
@Component
public class HeartbeatSender implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(HeartbeatSender.class);

    private final ScheduledExecutorService heartbeatExecutor;
    private final CoordinatorGrpcClient coordinatorClient;
    private final TaskExecutionEngine executionEngine;
    private final WorkerProperties workerProperties;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public HeartbeatSender(ScheduledExecutorService heartbeatExecutor,
                           CoordinatorGrpcClient coordinatorClient,
                           TaskExecutionEngine executionEngine,
                           WorkerProperties workerProperties) {
        this.heartbeatExecutor = heartbeatExecutor;
        this.coordinatorClient = coordinatorClient;
        this.executionEngine = executionEngine;
        this.workerProperties = workerProperties;
    }

    @Override
    public void start() {
        if (running.compareAndSet(false, true)) {
            long intervalMs = workerProperties.getHeartbeatIntervalMs();
            log.info("Starting heartbeat sender: intervalMs={}", intervalMs);

            heartbeatExecutor.scheduleAtFixedRate(
                    this::sendHeartbeat,
                    intervalMs,  // initial delay — let registration complete first
                    intervalMs,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    /**
     * Sends a single heartbeat to the coordinator.
     * Catches all exceptions to prevent the scheduled task from being cancelled.
     */
    private void sendHeartbeat() {
        try {
            String workerId = coordinatorClient.getWorkerId();
            if (workerId == null) {
                log.debug("Worker not yet registered, skipping heartbeat");
                return;
            }

            int runningTasks = executionEngine.getRunningTaskCount();
            int availableSlots = executionEngine.getAvailableSlots();

            coordinatorClient.sendHeartbeat(workerId, runningTasks, availableSlots);
            log.debug("Heartbeat sent: workerId={}, runningTasks={}, availableSlots={}",
                    workerId, runningTasks, availableSlots);

        } catch (Exception e) {
            log.warn("Failed to send heartbeat: {}", e.getMessage());
        }
    }

    @Override
    public void stop() {
        if (running.compareAndSet(true, false)) {
            log.info("Stopping heartbeat sender");
            heartbeatExecutor.shutdown();
            try {
                if (!heartbeatExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    heartbeatExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                heartbeatExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Start after other beans (especially registration service) are ready.
     */
    @Override
    public int getPhase() {
        return Integer.MAX_VALUE - 1;
    }
}
