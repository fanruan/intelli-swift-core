package com.fr.swift.executor.task.impl;

import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.Job.JobListener;
import com.fr.swift.executor.task.job.impl.UploadJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.segment.SegmentKey;

/**
 * This class created on 2019/2/14
 *
 * @author Lucifer
 * @description
 */
public class UploadExecutorTask extends AbstractExecutorTask<Job> {

    public UploadExecutorTask(SegmentKey uploadSegKey, boolean uploadWholeSeg, JobListener jobListener) {
        super(uploadSegKey.getTable(),
                true,
                ExecutorTaskType.UPLOAD,
                LockType.REAL_SEG,
                uploadSegKey.getId(),
                DBStatusType.ACTIVE,
                new UploadJob(uploadSegKey, uploadWholeSeg, jobListener));
    }

    public static UploadExecutorTask ofWholeSeg(SegmentKey uploadSegKey, JobListener jobListener) {
        return new UploadExecutorTask(uploadSegKey, true, jobListener);
    }

    public static UploadExecutorTask ofAllShowIndex(SegmentKey uploadSegKey) {
        return new UploadExecutorTask(uploadSegKey, false, null);
    }
}
