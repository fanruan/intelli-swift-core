package com.fr.swift.executor.task.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.config.service.SwiftSegmentService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                new CollateJob(sourceKey, segmentKeys.stream().map(SegmentKey::getId).collect(Collectors.toList())), 0);
    }

    public CollateExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType, LockType lockType,
                               String lockKey, DBStatusType dbStatusType, String taskId, long createTime, String taskContent,
                               int priority) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent, priority);
        SwiftSegmentService segmentService = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
        List list = JsonBuilder.readValue(taskContent, List.class);
        Set<String> segmentIds = new HashSet<>();
        for (Object o : list) {
            String segmentId = (String) o;
            segmentIds.add(segmentId);
        }
        List<SegmentKey> segmentKeyList = new ArrayList<>(segmentService.getByIds(segmentIds));
        this.job = new CollateJob(sourceKey, segmentKeyList.stream().map(SegmentKey::getId).collect(Collectors.toList()));
    }
}
