package com.fr.swift.executor.task.impl;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.impl.CollateJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.SwiftTaskType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/2/14
 *
 * @author Lucifer
 * @description
 */
public class CollateExecutorTask extends AbstractExecutorTask<Job> {

    public CollateExecutorTask(SourceKey sourceKey, List<SegmentKey> segmentKeys) throws Exception {
        super(sourceKey,
                true,
                SwiftTaskType.COLLATE,
                LockType.TABLE,
                sourceKey.getId(),
                DBStatusType.ACTIVE,
                new CollateJob(sourceKey, segmentKeys), 0);
    }

    public CollateExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType, LockType lockType,
                               String lockKey, DBStatusType dbStatusType, String taskId, long createTime, String taskContent,
                               int priority) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent, priority);
        List list = JsonBuilder.readValue(taskContent, List.class);
        List<SegmentKey> segmentKeyList = new ArrayList<SegmentKey>();
        for (Object o : list) {
            Map<String, Object> map = (Map<String, Object>) o;
            SegmentKey segmentKey = JsonBuilder.readValue(map, SwiftSegmentEntity.class);
            segmentKeyList.add(segmentKey);
        }
        this.job = new CollateJob(sourceKey, segmentKeyList);
    }
}
