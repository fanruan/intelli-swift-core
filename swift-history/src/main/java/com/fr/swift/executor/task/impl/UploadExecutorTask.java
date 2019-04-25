package com.fr.swift.executor.task.impl;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.Job.JobListener;
import com.fr.swift.executor.task.job.impl.UploadJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.Map;

/**
 * This class created on 2019/2/14
 *
 * @author Lucifer
 * @description
 */
public class UploadExecutorTask extends AbstractExecutorTask<Job> {

    public UploadExecutorTask(SegmentKey uploadSegKey, boolean uploadWholeSeg, JobListener jobListener) throws Exception {
        super(uploadSegKey.getTable(),
                true,
                ExecutorTaskType.UPLOAD,
                LockType.REAL_SEG,
                uploadSegKey.getId(),
                DBStatusType.ACTIVE,
                new UploadJob(uploadSegKey, uploadWholeSeg, jobListener));
    }

    public static UploadExecutorTask ofWholeSeg(SegmentKey uploadSegKey, JobListener jobListener) throws Exception {
        return new UploadExecutorTask(uploadSegKey, true, jobListener);
    }

    public static UploadExecutorTask ofWholeSeg(SegmentKey uploadSegKey) throws Exception {
        return new UploadExecutorTask(uploadSegKey, true, null);
    }

    public static UploadExecutorTask ofAllShowIndex(SegmentKey uploadSegKey) throws Exception {
        return new UploadExecutorTask(uploadSegKey, false, null);
    }

    public UploadExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType, LockType lockType,
                              String lockKey, DBStatusType dbStatusType, String taskId, long createTime, String taskContent) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent);
        Map<String, Object> contentMap = JsonBuilder.readValue(taskContent, Map.class);
        boolean uploadWholeSeg = (Boolean) contentMap.get("uploadWholeSeg");
        Map<String, Object> uploadSegmentKey = (Map<String, Object>) contentMap.get("uploadSegmentKey");
        SegmentKey segmentKey = JsonBuilder.readValue(uploadSegmentKey, SwiftSegmentEntity.class);
        this.job = new UploadJob(segmentKey, uploadWholeSeg, null);
    }
}
