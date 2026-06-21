package com.taskengine.coordinator.grpc;

import com.taskengine.common.dto.JobStatusResponse;
import com.taskengine.common.dto.JobSubmitRequest;
import com.taskengine.common.model.WorkerStatus;
import com.taskengine.common.util.IdGenerator;
import com.taskengine.coordinator.persistence.entity.WorkerEntity;
import com.taskengine.coordinator.persistence.repository.WorkerRepository;
import com.taskengine.coordinator.registry.WorkerRegistryService;
import com.taskengine.coordinator.service.JobService;
import com.taskengine.coordinator.service.TaskService;
import com.taskengine.proto.*;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * gRPC service implementation for the Coordinator.
 *
 * <p>This is the primary communication endpoint for workers and clients.
 * Extends the generated {@link CoordinatorServiceGrpc.CoordinatorServiceImplBase}
 * to implement all RPCs defined in {@code coordinator_service.proto}.
 *
 * <p>All methods include structured logging and proper error handling
 * with gRPC status codes.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@GrpcService
public class CoordinatorGrpcService extends CoordinatorServiceGrpc.CoordinatorServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(CoordinatorGrpcService.class);

    private final WorkerRepository workerRepository;
    private final WorkerRegistryService workerRegistryService;
    private final JobService jobService;
    private final TaskService taskService;

    public CoordinatorGrpcService(WorkerRepository workerRepository,
                                   WorkerRegistryService workerRegistryService,
                                   JobService jobService,
                                   TaskService taskService) {
        this.workerRepository = workerRepository;
        this.workerRegistryService = workerRegistryService;
        this.jobService = jobService;
        this.taskService = taskService;
    }

    /**
     * Handles worker registration.
     *
     * <p>Creates a new {@link WorkerEntity} in the database and registers
     * it in the in-memory worker registry with a gRPC channel.
     */
    @Override
    public void registerWorker(RegisterWorkerRequest request,
                                StreamObserver<RegisterWorkerResponse> responseObserver) {
        String hostname = request.getHostname();
        int port = request.getGrpcPort();
        int maxConcurrentTasks = request.getMaxConcurrentTasks();

        log.info("Worker registration request: hostname={}, port={}, maxConcurrentTasks={}",
                hostname, port, maxConcurrentTasks);

        try {
            // Create and persist worker entity
            WorkerEntity worker = new WorkerEntity();
            worker.setId(IdGenerator.generateUUID());
            worker.setHostname(hostname);
            worker.setPort(port);
            worker.setStatus(WorkerStatus.ACTIVE);
            worker.setLastHeartbeat(LocalDateTime.now());
            worker.setRunningTasks(0);
            worker.setMaxConcurrentTasks(maxConcurrentTasks > 0 ? maxConcurrentTasks : 4);

            worker = workerRepository.save(worker);

            // Register in the in-memory registry (creates gRPC channel)
            workerRegistryService.registerWorker(worker);

            String workerId = worker.getId().toString();
            log.info("Worker registered successfully: workerId={}, hostname={}, port={}",
                    workerId, hostname, port);

            responseObserver.onNext(RegisterWorkerResponse.newBuilder()
                    .setWorkerId(workerId)
                    .setSuccess(true)
                    .setMessage("Worker registered successfully")
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Failed to register worker: hostname={}, port={}", hostname, port, e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Worker registration failed: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Handles worker heartbeat.
     *
     * <p>Updates the worker's last heartbeat timestamp and running task count
     * in both the database and in-memory registry.
     */
    @Override
    public void heartbeat(HeartbeatRequest request,
                          StreamObserver<HeartbeatResponse> responseObserver) {
        String workerId = request.getWorkerId();
        int runningTasks = request.getRunningTasks();

        log.debug("Heartbeat received: workerId={}, runningTasks={}, availableSlots={}",
                workerId, runningTasks, request.getAvailableSlots());

        try {
            // Update in-memory registry
            workerRegistryService.updateHeartbeat(workerId, runningTasks);

            // Update database
            workerRepository.findById(UUID.fromString(workerId)).ifPresent(worker -> {
                worker.setLastHeartbeat(LocalDateTime.now());
                worker.setRunningTasks(runningTasks);
                worker.setStatus(WorkerStatus.ACTIVE);
                workerRepository.save(worker);
            });

            responseObserver.onNext(HeartbeatResponse.newBuilder()
                    .setAcknowledged(true)
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Failed to process heartbeat: workerId={}", workerId, e);
            responseObserver.onNext(HeartbeatResponse.newBuilder()
                    .setAcknowledged(false)
                    .build());
            responseObserver.onCompleted();
        }
    }

    /**
     * Handles job submission via gRPC.
     *
     * <p>Delegates to {@link JobService#submitJob} for job creation and task splitting.
     */
    @Override
    public void submitJob(SubmitJobRequest request,
                          StreamObserver<SubmitJobResponse> responseObserver) {
        String jobName = request.getName();
        String jobType = request.getType();

        log.info("Job submission received via gRPC: name='{}', type='{}'", jobName, jobType);

        try {
            JobSubmitRequest submitRequest = new JobSubmitRequest(
                    jobName, jobType, request.getInputData(), request.getParametersMap());

            JobStatusResponse result = jobService.submitJob(submitRequest);

            responseObserver.onNext(SubmitJobResponse.newBuilder()
                    .setJobId(result.getJobId())
                    .setSuccess(true)
                    .setMessage("Job submitted successfully with " + result.getTotalTasks() + " tasks")
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Failed to submit job: name='{}', type='{}'", jobName, jobType, e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Job submission failed: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Returns the current status of a job including all its tasks.
     */
    @Override
    public void getJobStatus(GetJobStatusRequest request,
                              StreamObserver<GetJobStatusResponse> responseObserver) {
        String jobId = request.getJobId();
        log.debug("Job status request: jobId={}", jobId);

        try {
            JobStatusResponse status = jobService.getJobStatus(jobId);

            JobInfo.Builder jobInfoBuilder = JobInfo.newBuilder()
                    .setJobId(status.getJobId())
                    .setName(status.getName())
                    .setType(status.getType())
                    .setTotalTasks(status.getTotalTasks())
                    .setCompletedTasks(status.getCompletedTasks())
                    .setFailedTasks(status.getFailedTasks());

            GetJobStatusResponse.Builder responseBuilder = GetJobStatusResponse.newBuilder()
                    .setJob(jobInfoBuilder.build());

            if (status.getTasks() != null) {
                for (var task : status.getTasks()) {
                    TaskInfo.Builder taskBuilder = TaskInfo.newBuilder()
                            .setTaskId(task.getTaskId())
                            .setType(task.getType())
                            .setRetryCount(task.getRetryCount())
                            .setMaxRetries(task.getMaxRetries());

                    if (task.getWorkerId() != null) {
                        taskBuilder.setWorkerId(task.getWorkerId());
                    }
                    if (task.getErrorMessage() != null) {
                        taskBuilder.setErrorMessage(task.getErrorMessage());
                    }

                    responseBuilder.addTasks(taskBuilder.build());
                }
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Failed to get job status: jobId={}", jobId, e);
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Job not found: " + jobId)
                    .asRuntimeException());
        }
    }

    /**
     * Handles task completion reports from workers.
     *
     * <p>Updates the task status and triggers job completion checking.
     */
    @Override
    public void reportTaskCompletion(ReportTaskCompletionRequest request,
                                      StreamObserver<ReportTaskCompletionResponse> responseObserver) {
        String workerId = request.getWorkerId();
        String taskId = request.getTaskId();
        long executionTimeMs = request.getExecutionTimeMs();

        log.info("Task completion reported: workerId={}, taskId={}, executionTimeMs={}",
                workerId, taskId, executionTimeMs);

        try {
            UUID jobId = taskService.completeTask(taskId, request.getResultData(), executionTimeMs);

            // Check if the parent job is now complete
            jobService.checkJobCompletion(jobId.toString());

            responseObserver.onNext(ReportTaskCompletionResponse.newBuilder()
                    .setAcknowledged(true)
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Failed to process task completion: taskId={}", taskId, e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to record task completion: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Handles task failure reports from workers.
     *
     * <p>Determines whether the task should be retried or marked as permanently failed.
     */
    @Override
    public void reportTaskFailure(ReportTaskFailureRequest request,
                                   StreamObserver<ReportTaskFailureResponse> responseObserver) {
        String workerId = request.getWorkerId();
        String taskId = request.getTaskId();
        String errorMessage = request.getErrorMessage();

        log.warn("Task failure reported: workerId={}, taskId={}, error='{}'",
                workerId, taskId, errorMessage);

        try {
            UUID jobId = taskService.failTask(taskId, errorMessage, request.getExecutionTimeMs());
            
            // Check if the parent job is now complete or failed
            jobService.checkJobCompletion(jobId.toString());

            responseObserver.onNext(ReportTaskFailureResponse.newBuilder()
                    .setAcknowledged(true)
                    .setWillRetry(true)  // The TaskService decides internally
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Failed to process task failure: taskId={}", taskId, e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to record task failure: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Handles job cancellation requests.
     */
    @Override
    public void cancelJob(CancelJobRequest request,
                          StreamObserver<CancelJobResponse> responseObserver) {
        String jobId = request.getJobId();
        log.info("Job cancellation request: jobId={}", jobId);

        try {
            jobService.cancelJob(jobId);

            responseObserver.onNext(CancelJobResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Job cancelled successfully")
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Failed to cancel job: jobId={}", jobId, e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to cancel job: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
