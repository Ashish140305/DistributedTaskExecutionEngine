package com.taskengine.coordinator.grpc;

import com.taskengine.coordinator.persistence.entity.TaskEntity;
import com.taskengine.coordinator.registry.WorkerRegistryService;
import com.taskengine.proto.*;

import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * gRPC client for communicating with Worker services.
 *
 * <p>Retrieves per-worker blocking stubs from the {@link WorkerRegistryService}
 * and uses them to send task assignments, cancellations, and status queries.
 *
 * <p>Unlike the standard {@code @GrpcClient} approach, this client manages
 * stubs dynamically because workers register at runtime with varying host:port
 * combinations. Each worker's stub is created during registration and stored
 * in the registry's {@link WorkerRegistryService.WorkerConnection}.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Service
public class WorkerGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(WorkerGrpcClient.class);

    private final WorkerRegistryService workerRegistryService;

    public WorkerGrpcClient(WorkerRegistryService workerRegistryService) {
        this.workerRegistryService = workerRegistryService;
    }

    /**
     * Assigns a task to a worker via gRPC.
     *
     * <p>Builds an {@link AssignTaskRequest} from the task entity and sends it
     * to the worker's gRPC service.
     *
     * @param workerId   the target worker's UUID string
     * @param taskEntity the task to assign
     * @return true if the worker accepted the task
     */
    public boolean assignTask(String workerId, TaskEntity taskEntity) {
        log.info("Sending task assignment: taskId={}, workerId={}", taskEntity.getId(), workerId);

        return workerRegistryService.getWorkerStub(workerId).map(stub -> {
            try {
                AssignTaskRequest request = AssignTaskRequest.newBuilder()
                        .setTaskId(taskEntity.getId().toString())
                        .setJobId(taskEntity.getJob().getId().toString())
                        .setType(taskEntity.getType())
                        .setInputData(taskEntity.getInputData() != null ? taskEntity.getInputData() : "")
                        .setRetryCount(taskEntity.getRetryCount())
                        .setMaxRetries(taskEntity.getMaxRetries())
                        .build();

                AssignTaskResponse response = stub.assignTask(request);

                if (response.getAccepted()) {
                    log.info("Task accepted by worker: taskId={}, workerId={}", taskEntity.getId(), workerId);
                } else {
                    log.warn("Task rejected by worker: taskId={}, workerId={}, reason={}",
                            taskEntity.getId(), workerId, response.getMessage());
                }

                return response.getAccepted();

            } catch (StatusRuntimeException e) {
                log.error("gRPC error assigning task: taskId={}, workerId={}, status={}, description={}",
                        taskEntity.getId(), workerId, e.getStatus().getCode(),
                        e.getStatus().getDescription(), e);
                return false;
            }
        }).orElseGet(() -> {
            log.error("No gRPC stub found for worker '{}'. Worker may have been removed.", workerId);
            return false;
        });
    }

    /**
     * Cancels a task on a worker via gRPC.
     *
     * @param workerId the worker's UUID string
     * @param taskId   the task ID to cancel
     * @param reason   the cancellation reason
     * @return true if the cancellation was successful
     */
    public boolean cancelTask(String workerId, String taskId, String reason) {
        log.info("Sending task cancellation: taskId={}, workerId={}, reason={}", taskId, workerId, reason);

        return workerRegistryService.getWorkerStub(workerId).map(stub -> {
            try {
                CancelTaskRequest request = CancelTaskRequest.newBuilder()
                        .setTaskId(taskId)
                        .setReason(reason != null ? reason : "Coordinator-initiated cancellation")
                        .build();

                CancelTaskResponse response = stub.cancelTask(request);

                if (response.getSuccess()) {
                    log.info("Task cancelled on worker: taskId={}, workerId={}", taskId, workerId);
                } else {
                    log.warn("Task cancellation failed on worker: taskId={}, workerId={}, message={}",
                            taskId, workerId, response.getMessage());
                }

                return response.getSuccess();

            } catch (StatusRuntimeException e) {
                log.error("gRPC error cancelling task: taskId={}, workerId={}, status={}",
                        taskId, workerId, e.getStatus().getCode(), e);
                return false;
            }
        }).orElseGet(() -> {
            log.error("No gRPC stub found for worker '{}' during cancellation", workerId);
            return false;
        });
    }

    /**
     * Queries a worker's current status via gRPC.
     *
     * @param workerId the worker's UUID string
     * @return the worker status response, or null if unreachable
     */
    public GetWorkerStatusResponse getWorkerStatus(String workerId) {
        log.debug("Querying worker status: workerId={}", workerId);

        return workerRegistryService.getWorkerStub(workerId).map(stub -> {
            try {
                GetWorkerStatusRequest request = GetWorkerStatusRequest.newBuilder()
                        .setWorkerId(workerId)
                        .build();

                return stub.getWorkerStatus(request);

            } catch (StatusRuntimeException e) {
                log.error("gRPC error querying worker status: workerId={}, status={}",
                        workerId, e.getStatus().getCode(), e);
                return null;
            }
        }).orElse(null);
    }
}
