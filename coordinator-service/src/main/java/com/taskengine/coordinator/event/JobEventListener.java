package com.taskengine.coordinator.event;

import com.taskengine.common.model.JobStatus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener for {@link JobStateChangeEvent} instances.
 *
 * <p>Handles cross-cutting concerns triggered by job state transitions:
 * <ul>
 *     <li>Structured logging of every state transition for audit</li>
 *     <li>Updating Micrometer counters for completed and failed jobs</li>
 * </ul>
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Component
public class JobEventListener {

    private static final Logger log = LoggerFactory.getLogger(JobEventListener.class);

    private final Counter jobsCompletedCounter;
    private final Counter jobsFailedCounter;
    private final Counter jobsCancelledCounter;

    /**
     * Constructs the listener and registers counters with the meter registry.
     *
     * @param meterRegistry the Micrometer meter registry
     */
    public JobEventListener(MeterRegistry meterRegistry) {
        this.jobsCompletedCounter = Counter.builder("coordinator.jobs.completed")
                .description("Total number of jobs completed successfully")
                .register(meterRegistry);
        this.jobsFailedCounter = Counter.builder("coordinator.jobs.failed")
                .description("Total number of jobs that failed")
                .register(meterRegistry);
        this.jobsCancelledCounter = Counter.builder("coordinator.jobs.cancelled")
                .description("Total number of jobs that were cancelled")
                .register(meterRegistry);
    }

    /**
     * Handles a job state change event.
     *
     * <p>Logs the transition and increments the appropriate metrics counter
     * when the job reaches a terminal state.
     *
     * @param event the job state change event
     */
    @EventListener
    public void onJobStateChange(JobStateChangeEvent event) {
        log.info("Job '{}' state transition: {} → {}",
                event.getJobId(), event.getOldStatus(), event.getNewStatus());

        JobStatus newStatus = event.getNewStatus();
        switch (newStatus) {
            case COMPLETED -> {
                jobsCompletedCounter.increment();
                log.info("Job '{}' completed successfully", event.getJobId());
            }
            case FAILED -> {
                jobsFailedCounter.increment();
                log.warn("Job '{}' has failed", event.getJobId());
            }
            case CANCELLED -> {
                jobsCancelledCounter.increment();
                log.info("Job '{}' was cancelled", event.getJobId());
            }
            default -> log.debug("Job '{}' transitioned to non-terminal state: {}",
                    event.getJobId(), newStatus);
        }
    }
}
