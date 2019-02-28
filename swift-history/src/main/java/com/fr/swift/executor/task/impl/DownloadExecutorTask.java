package com.fr.swift.executor.task.impl;

import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.impl.DownloadJob;
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
public class DownloadExecutorTask extends AbstractExecutorTask<Job> {

    public DownloadExecutorTask(SegmentKey downloadSegKey, boolean downloadWholeSeg, boolean replace) {
        super(downloadSegKey.getTable(),
                true,
                ExecutorTaskType.DOWNLOAD,
                LockType.REAL_SEG,
                LockType.REAL_SEG.name(),
                DBStatusType.ACTIVE,
                new DownloadJob(downloadSegKey, downloadWholeSeg, replace));
    }

    public static DownloadExecutorTask ofWholeSeg(SegmentKey downloadSegKey, boolean replace) {
        return new DownloadExecutorTask(downloadSegKey, true, replace);
    }

    public static DownloadExecutorTask ofAllShowIndex(SegmentKey downloadSegKey) {
        return new DownloadExecutorTask(downloadSegKey, false, false);
    }
}
