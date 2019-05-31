package com.fr.swift.executor.task.impl;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.impl.TransferJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.SwiftTaskType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

/**
 * This class created on 2019/2/14
 *
 * @author Lucifer
 * @description
 */
public class TransferExecutorTask extends AbstractExecutorTask<Job> {

    public TransferExecutorTask(SegmentKey transferSegKey) throws Exception {
        super(transferSegKey.getTable(),
                true,
                SwiftTaskType.TRANSFER,
                LockType.REAL_SEG,
                transferSegKey.getId(),
                DBStatusType.ACTIVE,
                new TransferJob(transferSegKey));
    }

    public TransferExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType, LockType lockType,
                                String lockKey, DBStatusType dbStatusType, String taskId, long createTime, String taskContent) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent);

        SegmentKey segmentKey = JsonBuilder.readValue(taskContent, SegmentKeyBean.class);
        this.job = new TransferJob(segmentKey);
    }
}
