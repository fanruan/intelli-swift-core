package com.fr.swift.executor.task.impl;

import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.impl.CollateJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * This class created on 2019/2/14
 *
 * @author Lucifer
 * @description
 */
public class CollateExecutorTask extends AbstractExecutorTask<Job> {

    public CollateExecutorTask(SourceKey sourceKey, List<SegmentKey> segmentKeys) {
        super(sourceKey,
                false,
                ExecutorTaskType.COLLATE,
                LockType.TABLE,
                sourceKey.getId(),
                DBStatusType.ACTIVE,
                new CollateJob(sourceKey, segmentKeys));
    }
}
