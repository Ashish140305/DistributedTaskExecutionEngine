package com.taskengine.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Entry point for the Worker Service.
 *
 * <p>The worker node registers with the coordinator on startup,
 * receives task assignments via gRPC, executes them concurrently
 * using a configurable thread pool, and reports results back.
 *
 * <p>Lifecycle:
 * <ol>
 *   <li>Spring context initialises; gRPC server starts on {@code grpc.server.port}</li>
 *   <li>{@code WorkerRegistrationService} registers with the coordinator</li>
 *   <li>{@code HeartbeatSender} begins periodic heartbeats</li>
 *   <li>Task assignments arrive via {@code WorkerGrpcService}</li>
 * </ol>
 *
 * @see com.taskengine.worker.registration.WorkerRegistrationService
 * @see com.taskengine.worker.heartbeat.HeartbeatSender
 * @see com.taskengine.worker.engine.TaskExecutionEngine
 */
@SpringBootApplication
@ConfigurationPropertiesScan("com.taskengine.worker.config")
public class WorkerApplication {

    /**
     * Launches the worker-service Spring Boot application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }
}
