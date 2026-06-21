package com.taskengine.coordinator.scheduler;

import com.taskengine.common.scheduler.SchedulingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Least-loaded scheduling strategy for distributing tasks across workers.
 *
 * <p>This scheduler selects the worker with the fewest currently running tasks,
 * ensuring that workload is concentrated on the least busy nodes. Only workers
 * with available capacity are considered eligible.
 *
 * <p>When multiple workers have the same minimum load, the first one encountered
 * is selected (stable ordering).
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Component("leastLoadedScheduler")
public class LeastLoadedScheduler implements SchedulingStrategy {

    private static final Logger log = LoggerFactory.getLogger(LeastLoadedScheduler.class);

    /**
     * Selects the worker with the minimum number of running tasks.
     *
     * @param workers the list of available worker snapshots
     * @return an {@link Optional} containing the least-loaded worker, or empty if none have capacity
     */
    @Override
    public Optional<WorkerSnapshot> selectWorker(List<WorkerSnapshot> workers) {
        if (workers == null || workers.isEmpty()) {
            log.debug("No workers available for least-loaded selection");
            return Optional.empty();
        }

        Optional<WorkerSnapshot> selected = workers.stream()
                .filter(w -> w.runningTasks() < w.maxConcurrentTasks())
                .min(Comparator.comparingInt(WorkerSnapshot::runningTasks));

        selected.ifPresentOrElse(
                w -> log.debug("Least-loaded selected worker '{}' (running={}/{})",
                        w.workerId(), w.runningTasks(), w.maxConcurrentTasks()),
                () -> log.debug("No workers with available capacity for least-loaded selection. " +
                        "Total workers: {}", workers.size())
        );

        return selected;
    }

    /**
     * Returns the name of this scheduling strategy.
     *
     * @return {@code "least-loaded"}
     */
    @Override
    public String getName() {
        return "least-loaded";
    }
}
