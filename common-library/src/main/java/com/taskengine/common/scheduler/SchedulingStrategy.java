package com.taskengine.common.scheduler;

import java.util.List;
import java.util.Optional;

/**
 * Strategy interface for task scheduling algorithms.
 *
 * <p>Determines which worker should receive the next task assignment.
 * The coordinator uses this to distribute workload across available workers.
 *
 * <p><b>Design Pattern:</b> Strategy Pattern — different scheduling algorithms
 * can be swapped at runtime without changing the scheduling engine.
 *
 * <p>Implementations:
 * <ul>
 *   <li>{@code RoundRobinScheduler} — cycles through workers in order</li>
 *   <li>{@code LeastLoadedScheduler} — picks worker with fewest running tasks</li>
 *   <li>{@code PriorityScheduler} — (future) priority-based scheduling</li>
 *   <li>{@code LocalityAwareScheduler} — (future) data-locality aware scheduling</li>
 * </ul>
 */
public interface SchedulingStrategy {

    /**
     * Selects a worker from the list of available workers to assign a task to.
     *
     * @param availableWorkers list of workers that can accept tasks (ACTIVE, has capacity)
     * @return an Optional containing the selected worker, or empty if no suitable worker is available
     */
    Optional<WorkerSnapshot> selectWorker(List<WorkerSnapshot> availableWorkers);

    /**
     * Returns the name of this scheduling strategy.
     *
     * @return the strategy name (e.g., "round-robin", "least-loaded")
     */
    String getName();

    /**
     * Immutable snapshot of a worker's state for scheduling decisions.
     * Avoids coupling the scheduler to JPA entities or gRPC messages.
     */
    record WorkerSnapshot(
            String workerId,
            int runningTasks,
            int maxConcurrentTasks
    ) {
        /**
         * Returns the number of available task slots on this worker.
         */
        public int availableSlots() {
            return maxConcurrentTasks - runningTasks;
        }

        /**
         * Checks if this worker has capacity for at least one more task.
         */
        public boolean hasCapacity() {
            return availableSlots() > 0;
        }
    }
}
