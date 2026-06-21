package com.taskengine.worker.grpc;

import com.taskengine.proto.*;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * gRPC client for communicating with the Coordinator service.
 *
 * <p>Uses the {@code @GrpcClient} annotation from grpc-spring-boot-starter
 * to inject a managed stub that handles channel lifecycle automatically.
 *
 * <p>All methods include error handling for {@link StatusRuntimeException}
 * and structured logging for observability.
 */
@Service
public class CoordinatorGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(CoordinatorGrpcClient.class);

    @GrpcClient("coordinator")
    private CoordinatorServiceGrpc.CoordinatorServiceBlockingStub coordinatorStub;

    private volatile String workerId;

    /**
     * Registers this worker with the coordinator.
     *
     * @param hostname            the worker's hostname
     * @param port                the worker's gRPC port
     * @param maxConcurrentTasks  the maximum tasks this worker can run simultaneously
     * @return the assigned worker ID
     */
    public String registerWorker(String hostname, int port, int maxConcurrentTasks) {
        log.info("Registering worker with coordinator: hostname={}, port={}, maxConcurrentTasks={}",
                hostname, port, maxConcurrentTasks);

        try {
            RegisterWorkerRequest request = RegisterWorkerRequest.newBuilder()
                    .setHostname(hostname)
                    .setGrpcPort(port)
                    .setMaxConcurrentTasks(maxConcurrentTasks)
                    .build();

            RegisterWorkerResponse response = coordinatorStub.registerWorker(request);

            if (response.getSuccess()) {
                this.workerId = response.getWorkerId();
                log.info("Worker registered successfully: workerId={}, message={}",
                        workerId, response.getMessage());
                return workerId;
            } else {
                log.error("Worker registration failed: message={}", response.getMessage());
                throw new RuntimeException("Worker registration failed: " + response.getMessage());
            }
        } catch (StatusRuntimeException e) {
            log.error("gRPC error during worker registration: status={}, description={}",
                    e.getStatus().getCode(), e.getStatus().getDescription(), e);
            throw new RuntimeException("Failed to register worker with coordinator", e);
        }
    }

    /**
     * Sends a heartbeat to the coordinator.
     *
     * @param workerId       this worker's ID
     * @param runningTasks   number of currently running tasks
     * @param availableSlots number of available task slots
     */
    public void sendHeartbeat(String workerId, int runningTasks, int availableSlots) {
        try {
            HeartbeatRequest request = HeartbeatRequest.newBuilder()
                    .setWorkerId(workerId)
                    .setRunningTasks(runningTasks)
                    .setAvailableSlots(availableSlots)
                    .build();

            HeartbeatResponse response = coordinatorStub.heartbeat(request);

            if (response.getAcknowledged()) {
                log.debug("Heartbeat acknowledged: workerId={}, runningTasks={}", workerId, runningTasks);
            }

            // Handle any cancel commands from coordinator
            if (response.getCancelTaskIdsList() != null && !response.getCancelTaskIdsList().isEmpty()) {
                log.info("Coordinator requested task cancellations: {}", response.getCancelTaskIdsList());
            }
        } catch (StatusRuntimeException e) {
            log.warn("Heartbeat failed: workerId={}, status={}, description={}",
                    workerId, e.getStatus().getCode(), e.getStatus().getDescription());
        }
    }

    /**
     * Reports successful task completion to the coordinator.
     *
     * @param workerId        this worker's ID
     * @param taskId          the completed task ID
     * @param resultData      the task result data
     * @param executionTimeMs the execution time in milliseconds
     */
    public void reportTaskCompletion(String workerId, String taskId, String resultData, long executionTimeMs) {
        log.info("Reporting task completion: workerId={}, taskId={}, executionTimeMs={}",
                workerId, taskId, executionTimeMs);

        try {
            ReportTaskCompletionRequest request = ReportTaskCompletionRequest.newBuilder()
                    .setWorkerId(workerId)
                    .setTaskId(taskId)
                    .setResultData(resultData != null ? resultData : "")
                    .setExecutionTimeMs(executionTimeMs)
                    .build();

            ReportTaskCompletionResponse response = coordinatorStub.reportTaskCompletion(request);

            if (response.getAcknowledged()) {
                log.info("Task completion acknowledged: taskId={}", taskId);
            } else {
                log.warn("Task completion not acknowledged: taskId={}", taskId);
            }
        } catch (StatusRuntimeException e) {
            log.error("Failed to report task completion: taskId={}, status={}, description={}",
                    taskId, e.getStatus().getCode(), e.getStatus().getDescription(), e);
        }
    }

    /**
     * Reports task failure to the coordinator.
     *
     * @param workerId        this worker's ID
     * @param taskId          the failed task ID
     * @param errorMessage    the error message
     * @param executionTimeMs the execution time in milliseconds
     */
    public void reportTaskFailure(String workerId, String taskId, String errorMessage, long executionTimeMs) {
        log.info("Reporting task failure: workerId={}, taskId={}, error={}, executionTimeMs={}",
                workerId, taskId, errorMessage, executionTimeMs);

        try {
            ReportTaskFailureRequest request = ReportTaskFailureRequest.newBuilder()
                    .setWorkerId(workerId)
                    .setTaskId(taskId)
                    .setErrorMessage(errorMessage != null ? errorMessage : "Unknown error")
                    .setExecutionTimeMs(executionTimeMs)
                    .build();

            ReportTaskFailureResponse response = coordinatorStub.reportTaskFailure(request);

            log.info("Task failure reported: taskId={}, willRetry={}", taskId, response.getWillRetry());
        } catch (StatusRuntimeException e) {
            log.error("Failed to report task failure: taskId={}, status={}, description={}",
                    taskId, e.getStatus().getCode(), e.getStatus().getDescription(), e);
        }
    }

    /**
     * Returns the assigned worker ID.
     */
    public String getWorkerId() {
        return workerId;
    }

    /**
     * Sets the worker ID (used during registration).
     */
    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }
}
