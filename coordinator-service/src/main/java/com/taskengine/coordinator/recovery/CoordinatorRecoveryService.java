package com.taskengine.coordinator.recovery;

import com.taskengine.coordinator.fault.FaultRecoveryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Coordinator startup recovery service.
 *
 * <p>Implements {@link ApplicationRunner} to automatically recover
 * unfinished jobs and tasks when the coordinator restarts.
 *
 * <p>This ensures no work is lost across coordinator restarts by
 * re-queuing tasks that were in non-terminal states when the
 * previous coordinator instance went down.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Component
public class CoordinatorRecoveryService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CoordinatorRecoveryService.class);

    private final FaultRecoveryService faultRecoveryService;

    public CoordinatorRecoveryService(FaultRecoveryService faultRecoveryService) {
        this.faultRecoveryService = faultRecoveryService;
    }

    /**
     * Runs recovery on application startup.
     *
     * <p>Delegates to {@link FaultRecoveryService#recoverUnfinishedJobs()}
     * to scan for and re-queue interrupted work.
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("=== Coordinator Recovery Service Starting ===");
        log.info("Scanning database for unfinished jobs and tasks from previous run...");

        try {
            FaultRecoveryService.RecoverySummary summary = faultRecoveryService.recoverUnfinishedJobs();

            if (summary.jobsRecovered() == 0) {
                log.info("No unfinished jobs found. Clean startup.");
            } else {
                log.info("Recovery complete: {} job(s) recovered, {} task(s) re-queued, {} task(s) failed",
                        summary.jobsRecovered(), summary.tasksReQueued(), summary.tasksFailed());
            }
        } catch (Exception e) {
            log.error("Recovery failed! Manual intervention may be required.", e);
        }

        log.info("=== Coordinator Recovery Service Complete ===");
    }
}
