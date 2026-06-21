package com.taskengine.coordinator.event;

import com.taskengine.common.model.JobStatus;
import org.springframework.context.ApplicationEvent;

/**
 * Spring application event published when a job undergoes a state transition.
 *
 * <p>This event enables loose coupling between the core job lifecycle services
 * and cross-cutting concerns such as metrics collection and audit logging.
 * Listeners receive the job ID, previous status, and new status.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
public class JobStateChangeEvent extends ApplicationEvent {

    private final String jobId;
    private final JobStatus oldStatus;
    private final JobStatus newStatus;

    /**
     * Creates a new job state change event.
     *
     * @param source    the object that published the event
     * @param jobId     the UUID string of the job
     * @param oldStatus the previous job status
     * @param newStatus the new job status
     */
    public JobStateChangeEvent(Object source, String jobId, JobStatus oldStatus, JobStatus newStatus) {
        super(source);
        this.jobId = jobId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    /**
     * Returns the ID of the job whose state changed.
     *
     * @return the job UUID string
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Returns the previous status of the job.
     *
     * @return the old job status
     */
    public JobStatus getOldStatus() {
        return oldStatus;
    }

    /**
     * Returns the new status of the job.
     *
     * @return the new job status
     */
    public JobStatus getNewStatus() {
        return newStatus;
    }

    @Override
    public String toString() {
        return "JobStateChangeEvent{" +
                "jobId='" + jobId + '\'' +
                ", oldStatus=" + oldStatus +
                ", newStatus=" + newStatus +
                '}';
    }
}
