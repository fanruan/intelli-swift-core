package com.fr.swift.executor.task.impl;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.impl.DownloadJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.SwiftTaskType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.Map;

/**
 * This class created on 2019/2/14
 *
 * @author Lucifer
 * @description
 */
public class DownloadExecutorTask extends AbstractExecutorTask<Job> {

    public DownloadExecutorTask(SegmentKey downloadSegKey, boolean downloadWholeSeg, boolean replace) throws Exception {
        super(downloadSegKey.getTable(),
                true,
                SwiftTaskType.DOWNLOAD,
                LockType.REAL_SEG,
                LockType.REAL_SEG.name(),
                DBStatusType.ACTIVE,
                new DownloadJob(downloadSegKey, downloadWholeSeg, replace));
    }

    public static DownloadExecutorTask ofWholeSeg(SegmentKey downloadSegKey, boolean replace) throws Exception {
        return new DownloadExecutorTask(downloadSegKey, true, replace);
    }

    public static DownloadExecutorTask ofAllShowIndex(SegmentKey downloadSegKey) throws Exception {
        return new DownloadExecutorTask(downloadSegKey, false, false);
    }

    public DownloadExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType, LockType lockType,
                                String lockKey, DBStatusType dbStatusType, String taskId, long createTime, String taskContent) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent);
        Map<String, Object> contentMap = JsonBuilder.readValue(taskContent, Map.class);
        boolean downloadWholeSeg = (Boolean) contentMap.get("downloadWholeSeg");
        boolean replace = (Boolean) contentMap.get("replace");
        Map<String, Object> downloadSegKey = (Map<String, Object>) contentMap.get("downloadSegKey");
        SegmentKey segmentKey = JsonBuilder.readValue(downloadSegKey, SwiftSegmentEntity.class);
        this.job = new DownloadJob(segmentKey, downloadWholeSeg, replace);
    }
}
