package com.taskengine.coordinator.persistence.repository;

import com.taskengine.common.model.WorkerStatus;
import com.taskengine.coordinator.persistence.entity.WorkerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link WorkerEntity}.
 *
 * <p>Provides queries for worker management including status filtering
 * and heartbeat-based staleness detection.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Repository
public interface WorkerRepository extends JpaRepository<WorkerEntity, UUID> {

    /**
     * Finds all workers with the specified status.
     *
     * @param status the worker status to filter by
     * @return list of workers with the given status
     */
    List<WorkerEntity> findByStatus(WorkerStatus status);

    /**
     * Finds all workers whose last heartbeat is before the given timestamp.
     * Used to detect workers that have missed heartbeat deadlines.
     *
     * @param timestamp the cutoff timestamp
     * @return list of workers whose last heartbeat predates the cutoff
     */
    List<WorkerEntity> findByLastHeartbeatBefore(LocalDateTime timestamp);
}
