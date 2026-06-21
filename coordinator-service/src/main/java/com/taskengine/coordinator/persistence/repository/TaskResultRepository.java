package com.taskengine.coordinator.persistence.repository;

import com.taskengine.coordinator.persistence.entity.TaskResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link TaskResultEntity}.
 *
 * <p>Provides queries for retrieving task execution results,
 * primarily for audit and diagnostic purposes.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Repository
public interface TaskResultRepository extends JpaRepository<TaskResultEntity, UUID> {

    /**
     * Finds all result records for a specific task, representing all execution attempts.
     *
     * @param taskId the task's UUID
     * @return list of result entities, one per execution attempt
     */
    List<TaskResultEntity> findByTaskId(UUID taskId);
}
