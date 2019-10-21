package com.fr.swift.executor.task.impl;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.config.entity.SwiftSegmentEntity;
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

    public TransferExecutorTask(SegmentKey transferSegKey, LockType lockType) throws Exception {
        super(transferSegKey.getTable(),
                true,
                SwiftTaskType.TRANSFER,
                lockType,
                transferSegKey.getId(),
                DBStatusType.ACTIVE,
                new TransferJob(transferSegKey), 0);
    }

    public TransferExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType, LockType lockType,
                                String lockKey, DBStatusType dbStatusType, String taskId, long createTime, String taskContent,
                                int priority) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent, priority);

        SegmentKey segmentKey = JsonBuilder.readValue(taskContent, SwiftSegmentEntity.class);
        this.job = new TransferJob(segmentKey);
    }

    /**
     * 主动触发的，如满了之后transfer的
     *
     * @param transferSegKey seg key
     * @return task
     * @throws Exception 异常
     */
    public static TransferExecutorTask ofActive(SegmentKey transferSegKey) throws Exception {
        return new TransferExecutorTask(transferSegKey, LockType.REAL_SEG);
    }

    /**
     * 被动触发的，如定时器transfer的
     * 需要和insert互斥，以防transfer正在insert的seg
     *
     * @param transferSegKey seg key
     * @return task
     * @throws Exception 异常
     */
    public static TransferExecutorTask ofPassive(SegmentKey transferSegKey) throws Exception {
        return new TransferExecutorTask(transferSegKey, LockType.VIRTUAL_SEG);
    }
}
