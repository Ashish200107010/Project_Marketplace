package com.certplatform.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.certplatform.job.entity.Job;
import com.certplatform.job.enums.JobStatus;

import java.util.List;

/**
 * Repository interface for Job entity.
 * Provides CRUD operations and custom queries for job management.
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    /**
     * Find the top 10 jobs by status, ordered by creation time ascending.
     *
     * @param status the job status filter
     * @return list of jobs matching the status
     */
    List<Job> findTop10ByStatusOrderByCreatedAtAsc(JobStatus status);
}
