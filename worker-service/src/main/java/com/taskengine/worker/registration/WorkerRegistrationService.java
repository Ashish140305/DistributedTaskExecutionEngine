package com.taskengine.worker.registration;

import com.taskengine.worker.config.WorkerProperties;
import com.taskengine.worker.grpc.CoordinatorGrpcClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * Handles worker registration with the Coordinator on startup.
 *
 * <p>Implements {@link ApplicationRunner} to register immediately after
 * the Spring context is fully initialized. Retries registration up to
 * 3 times with a 5-second delay between attempts.
 *
 * <p>On successful registration, stores the assigned worker ID and
 * makes it available for heartbeat and task reporting.
 */
@Component
public class WorkerRegistrationService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(WorkerRegistrationService.class);
    private static final int MAX_REGISTRATION_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 5000;

    private final CoordinatorGrpcClient coordinatorClient;
    private final WorkerProperties workerProperties;

    @Value("${grpc.server.port:9091}")
    private int grpcPort;

    @Value("${worker.hostname:#{null}}")
    private String configuredHostname;

    private volatile String workerId;
    private volatile String hostname;

    public WorkerRegistrationService(CoordinatorGrpcClient coordinatorClient,
                                     WorkerProperties workerProperties) {
        this.coordinatorClient = coordinatorClient;
        this.workerProperties = workerProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        registerWithRetry();
    }

    /**
     * Attempts to register with the coordinator, retrying on failure.
     */
    private void registerWithRetry() {
        this.hostname = resolveHostname();

        for (int attempt = 1; attempt <= MAX_REGISTRATION_RETRIES; attempt++) {
            try {
                log.info("Attempting worker registration: attempt={}/{}, hostname={}, grpcPort={}, maxTasks={}",
                        attempt, MAX_REGISTRATION_RETRIES, hostname, grpcPort,
                        workerProperties.getMaxConcurrentTasks());

                this.workerId = coordinatorClient.registerWorker(
                        hostname,
                        grpcPort,
                        workerProperties.getMaxConcurrentTasks()
                );

                log.info("Worker registration successful: workerId={}, hostname={}, port={}",
                        workerId, hostname, grpcPort);
                return;

            } catch (Exception e) {
                log.error("Worker registration failed: attempt={}/{}, error={}",
                        attempt, MAX_REGISTRATION_RETRIES, e.getMessage());

                if (attempt < MAX_REGISTRATION_RETRIES) {
                    try {
                        log.info("Retrying registration in {}ms...", RETRY_DELAY_MS);
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Registration retry interrupted");
                        return;
                    }
                } else {
                    log.error("All registration attempts exhausted. Worker will not be available for task execution.");
                }
            }
        }
    }

    /**
     * Resolves the hostname for this worker.
     * Uses configured hostname if available, otherwise auto-detects.
     */
    private String resolveHostname() {
        if (configuredHostname != null && !configuredHostname.isBlank()) {
            log.info("Using configured hostname: {}", configuredHostname);
            return configuredHostname;
        }
        try {
            String detected = InetAddress.getLocalHost().getHostName();
            log.info("Auto-detected hostname: {}", detected);
            return detected;
        } catch (Exception e) {
            log.warn("Failed to detect hostname, using 'localhost': {}", e.getMessage());
            return "localhost";
        }
    }

    public String getWorkerId() {
        return workerId;
    }

    public String getHostname() {
        return hostname != null ? hostname : "unknown";
    }

    public int getGrpcPort() {
        return grpcPort;
    }

    public int getMaxConcurrentTasks() {
        return workerProperties.getMaxConcurrentTasks();
    }
}
