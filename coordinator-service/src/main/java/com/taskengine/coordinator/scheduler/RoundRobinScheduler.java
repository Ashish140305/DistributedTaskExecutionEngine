package com.taskengine.coordinator.scheduler;

import com.taskengine.common.scheduler.SchedulingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round-robin scheduling strategy for distributing tasks across workers.
 *
 * <p>This scheduler maintains an atomic counter and selects workers in a circular
 * fashion. Only workers with available capacity (running tasks &lt; max concurrent tasks)
 * are considered eligible for assignment. The counter increments on each selection
 * attempt, ensuring fair distribution across the worker pool.
 *
 * <p>This is the primary (default) scheduling strategy used by the task scheduler.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Component("roundRobinScheduler")
public class RoundRobinScheduler implements SchedulingStrategy {

    private static final Logger log = LoggerFactory.getLogger(RoundRobinScheduler.class);

    private final AtomicInteger counter = new AtomicInteger(0);

    /**
     * Selects the next worker with available capacity using round-robin selection.
     *
     * <p>The algorithm filters workers that have capacity ({@code runningTasks < maxConcurrentTasks}),
     * then uses a modulo operation on the internal counter to pick the next worker in sequence.
     *
     * @param workers the list of available worker snapshots
     * @return an {@link Optional} containing the selected worker, or empty if no workers have capacity
     */
    @Override
    public Optional<WorkerSnapshot> selectWorker(List<WorkerSnapshot> workers) {
        if (workers == null || workers.isEmpty()) {
            log.debug("No workers available for round-robin selection");
            return Optional.empty();
        }

        List<WorkerSnapshot> eligible = workers.stream()
                .filter(w -> w.runningTasks() < w.maxConcurrentTasks())
                .toList();

        if (eligible.isEmpty()) {
            log.debug("No workers with available capacity for round-robin selection. Total workers: {}",
                    workers.size());
            return Optional.empty();
        }

        int index = Math.abs(counter.getAndIncrement() % eligible.size());
        WorkerSnapshot selected = eligible.get(index);

        log.debug("Round-robin selected worker '{}' (index={}, running={}/{})",
                selected.workerId(), index, selected.runningTasks(), selected.maxConcurrentTasks());

        return Optional.of(selected);
    }

    /**
     * Returns the name of this scheduling strategy.
     *
     * @return {@code "round-robin"}
     */
    @Override
    public String getName() {
        return "round-robin";
    }
}
