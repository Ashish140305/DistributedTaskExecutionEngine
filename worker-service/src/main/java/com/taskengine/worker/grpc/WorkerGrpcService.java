package com.taskengine.worker.grpc;

import com.taskengine.proto.*;
import com.taskengine.worker.engine.TaskExecutionEngine;
import com.taskengine.worker.registration.WorkerRegistrationService;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * gRPC service implementation for the Worker.
 *
 * <p>Handles incoming RPCs from the Coordinator:
 * <ul>
 *   <li>{@code AssignTask} — accepts and queues a task for execution</li>
 *   <li>{@code CancelTask} — cancels a running task</li>
 *   <li>{@code GetWorkerStatus} — returns current worker health and load</li>
 * </ul>
 */
@GrpcService
public class WorkerGrpcService extends WorkerServiceGrpc.WorkerServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(WorkerGrpcService.class);

    private final TaskExecutionEngine executionEngine;
    private final WorkerRegistrationService registrationService;

    public WorkerGrpcService(TaskExecutionEngine executionEngine,
                             WorkerRegistrationService registrationService) {
        this.executionEngine = executionEngine;
        this.registrationService = registrationService;
    }

    /**
     * Handles task assignment from the Coordinator.
     * Validates the request and delegates to the execution engine.
     */
    @Override
    public void assignTask(AssignTaskRequest request, StreamObserver<AssignTaskResponse> responseObserver) {
        String taskId = request.getTaskId();
        String taskType = request.getType();

        log.info("Received task assignment: taskId={}, type={}, jobId={}", taskId, taskType, request.getJobId());

        try {
            // Check if we have capacity
            if (executionEngine.getAvailableSlots() <= 0) {
                log.warn("No available slots for task: taskId={}", taskId);
                responseObserver.onNext(AssignTaskResponse.newBuilder()
                        .setAccepted(false)
                        .setMessage("Worker has no available task slots")
                        .build());
                responseObserver.onCompleted();
                return;
            }

            // Submit the task for execution
            boolean accepted = executionEngine.submitTask(request);

            AssignTaskResponse response = AssignTaskResponse.newBuilder()
                    .setAccepted(accepted)
                    .setMessage(accepted ? "Task accepted for execution" : "Task rejected")
                    .build();

            log.info("Task assignment response: taskId={}, accepted={}", taskId, accepted);
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error handling task assignment: taskId={}", taskId, e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to assign task: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Handles task cancellation from the Coordinator.
     */
    @Override
    public void cancelTask(CancelTaskRequest request, StreamObserver<CancelTaskResponse> responseObserver) {
        String taskId = request.getTaskId();
        String reason = request.getReason();

        log.info("Received task cancellation: taskId={}, reason={}", taskId, reason);

        try {
            boolean cancelled = executionEngine.cancelTask(taskId);

            CancelTaskResponse response = CancelTaskResponse.newBuilder()
                    .setSuccess(cancelled)
                    .setMessage(cancelled ? "Task cancelled successfully" : "Task not found or already completed")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error cancelling task: taskId={}", taskId, e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to cancel task: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Returns current worker status including running tasks.
     */
    @Override
    public void getWorkerStatus(GetWorkerStatusRequest request, StreamObserver<GetWorkerStatusResponse> responseObserver) {
        log.debug("Received worker status request");

        try {
            String workerId = registrationService.getWorkerId();

            WorkerInfo workerInfo = WorkerInfo.newBuilder()
                    .setWorkerId(workerId != null ? workerId : "unregistered")
                    .setHostname(registrationService.getHostname())
                    .setPort(registrationService.getGrpcPort())
                    .setStatus(com.taskengine.proto.WorkerStatus.WORKER_STATUS_ACTIVE)
                    .setRunningTasks(executionEngine.getRunningTaskCount())
                    .setMaxConcurrentTasks(registrationService.getMaxConcurrentTasks())
                    .setLastHeartbeatMs(System.currentTimeMillis())
                    .build();

            // Build running tasks info
            List<TaskInfo> runningTaskInfos = new ArrayList<>();
            for (String taskId : executionEngine.getRunningTaskIds()) {
                runningTaskInfos.add(TaskInfo.newBuilder()
                        .setTaskId(taskId)
                        .setStatus(com.taskengine.proto.TaskStatus.TASK_STATUS_RUNNING)
                        .build());
            }

            GetWorkerStatusResponse response = GetWorkerStatusResponse.newBuilder()
                    .setWorker(workerInfo)
                    .addAllRunningTasks(runningTaskInfos)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error getting worker status", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to get worker status: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
