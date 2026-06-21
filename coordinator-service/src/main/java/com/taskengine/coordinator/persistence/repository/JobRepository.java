package com.taskengine.coordinator.persistence.repository;

import com.taskengine.common.model.JobStatus;
import com.taskengine.coordinator.persistence.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link JobEntity}.
 *
 * <p>Provides standard CRUD operations plus custom queries for retrieving
 * jobs by their lifecycle status.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@Repository
public interface JobRepository extends JpaRepository<JobEntity, UUID> {

    /**
     * Finds all jobs with the specified status.
     *
     * @param status the job status to filter by
     * @return list of matching jobs, ordered by creation time descending
     */
    List<JobEntity> findByStatus(JobStatus status);

    /**
     * Finds all jobs whose status is in the given collection of statuses.
     *
     * @param statuses the set of statuses to match against
     * @return list of matching jobs
     */
    List<JobEntity> findByStatusIn(Collection<JobStatus> statuses);
}
