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
import com.fr.swift.property.MigrateProperty;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Moira
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class MigrateExecutorTask extends AbstractExecutorTask<MigrateJob> {
    public MigrateExecutorTask(Map<File, SegmentKey> segments, boolean remote, String path, String prePath) throws Exception {
        super(new SourceKey(SwiftTaskType.MIGRATE.name()),
                true,
                SwiftTaskType.MIGRATE,
                LockType.TABLE,
                "migrate",
                DBStatusType.ACTIVE,
                new MigrateJob(segments, remote, path, prePath), 0);
    }

    public static ExecutorTask of(Map<File, SegmentKey> segments, boolean remote, String path, String prePath, String remoteUrl) throws Exception {
        return new MigrateExecutorTask(segments, remote, path, prePath);
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
        Map<File, SegmentKey> segments = SwiftContext.get().getBean(SegmentService.class).getSegmentKeys(segmentIds)
                .stream().map(r -> new Pair<>(new File(SegmentUtils.newSegment(r).getLocation().getAbsolutePath()), r))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        this.job = new MigrateJob(segments, MigrateProperty.get().isRemote(), MigrateProperty.get().getBackupPath(), SwiftProperty.get().getCubesPath());
    }
}
