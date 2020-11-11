package com.fr.swift.executor.task.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.job.impl.MigrateJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.SwiftTaskType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Moira
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class MigrateExecutorTask extends AbstractExecutorTask<MigrateJob> {
    public MigrateExecutorTask(List<SegmentKey> segmentKeys, String path) throws Exception {
        super(new SourceKey(SwiftTaskType.MIGRATE.name()),
                true,
                SwiftTaskType.MIGRATE,
                LockType.TABLE,
                "migrate",
                DBStatusType.ACTIVE,
                new MigrateJob(0, path, ""), 0);
    }

    public static ExecutorTask of(List<SegmentKey> segmentKeys, String path) throws Exception {
        return new MigrateExecutorTask(segmentKeys, path);
    }

    public MigrateExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType, LockType lockType,
                               String lockKey, DBStatusType dbStatusType, String taskId, long createTime, String taskContent,
                               int priority) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent, priority);

        List list = JsonBuilder.readValue(taskContent, List.class);
        List<String> segmentIds = new ArrayList<>();
        for (Object o : list) {
            String segmentId = (String) o;
            segmentIds.add(segmentId);
        }
        List<SegmentKey> segmentKeys = SwiftContext.get().getBean(SegmentService.class).getSegmentKeys((SourceKey) segmentIds);
//        this.job = new MigrateJob(segmentKeys, MigrateProperty.get().getBackupPath());
    }
}
