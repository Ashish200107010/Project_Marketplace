package com.certplatform.submission.dto;

import java.util.List;

import com.certplatform.job.entity.Job;

public interface JobQueue {
    void enqueue(ReviewJob job);
    List<Job> pollBatch(int batchSize);
}
