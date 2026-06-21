package com.taskengine.coordinator.persistence.repository;

import com.taskengine.common.model.TaskStatus;
import com.taskengine.coordinator.persistence.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link TaskEntity}.
 *
 * <p>Provides queries for task lifecycle management including retrieval by
 * job, worker, status, and aggregate counting for job completion tracking.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    /**
     * Finds all tasks belonging to a specific job.
     *
     * @param jobId the parent job's UUID
     * @return list of tasks for the given job
     */
    List<TaskEntity> findByJobId(UUID jobId);

    /**
     * Finds all tasks with the specified status.
     *
     * @param status the task status to filter by
     * @return list of matching tasks
     */
    List<TaskEntity> findByStatus(TaskStatus status);

    /**
     * Finds all tasks assigned to a specific worker with any of the given statuses.
     *
     * @param workerId the worker's UUID
     * @param statuses the set of statuses to match
     * @return list of matching tasks
     */
    List<TaskEntity> findByWorkerIdAndStatusIn(UUID workerId, Collection<TaskStatus> statuses);

    /**
     * Finds all tasks belonging to a job with a specific status.
     *
     * @param jobId  the parent job's UUID
     * @param status the task status to filter by
     * @return list of matching tasks
     */
    List<TaskEntity> findByJobIdAndStatus(UUID jobId, TaskStatus status);

    /**
     * Counts the number of tasks for a given job with a specific status.
     *
     * @param jobId  the parent job's UUID
     * @param status the task status to count
     * @return the count of matching tasks
     */
    long countByJobIdAndStatus(UUID jobId, TaskStatus status);

    /**
     * Finds all tasks whose status is in the given collection.
     *
     * @param statuses the set of statuses to match
     * @return list of matching tasks
     */
    List<TaskEntity> findByStatusIn(Collection<TaskStatus> statuses);

    /**
     * Counts the number of tasks with the given status.
     *
     * @param status the task status to count
     * @return the count of matching tasks
     */
    long countByStatus(TaskStatus status);

    /**
     * Counts the number of tasks belonging to a specific job with any of the given statuses.
     *
     * @param jobId    the parent job's UUID
     * @param statuses the set of statuses to count
     * @return the count of matching tasks
     */
    @Query("SELECT COUNT(t) FROM TaskEntity t WHERE t.job.id = :jobId AND t.status IN :statuses")
    long countByJobIdAndStatusIn(@Param("jobId") UUID jobId, @Param("statuses") Collection<TaskStatus> statuses);
}
