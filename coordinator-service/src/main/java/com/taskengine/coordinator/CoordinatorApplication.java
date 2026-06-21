package com.taskengine.coordinator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the Coordinator Service.
 *
 * <p>The coordinator is the central control plane of the Distributed Task Execution Engine.
 * It is responsible for:
 * <ul>
 *     <li>Accepting job submissions via REST and gRPC APIs</li>
 *     <li>Splitting jobs into parallelizable tasks</li>
 *     <li>Managing worker registration and heartbeat monitoring</li>
 *     <li>Scheduling tasks to available workers using pluggable strategies</li>
 *     <li>Tracking task and job lifecycle state transitions</li>
 *     <li>Performing fault recovery on worker failures and coordinator restarts</li>
 * </ul>
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class CoordinatorApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CoordinatorApplication.class, args);
    }
}
