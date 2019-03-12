package com.fr.swift.executor.task.impl;

import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.impl.IndexJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.segment.SegmentKey;

/**
 * This class created on 2019/2/14
 *
 * @author Lucifer
 * @description 索引先放在transfer里一起，暂时弃用
 */
public class IndexExecutorTask extends AbstractExecutorTask<Job> {

    public IndexExecutorTask(SegmentKey indexSegKey) throws Exception {
        super(indexSegKey.getTable(),
                true,
                ExecutorTaskType.INDEX,
                LockType.REAL_SEG,
                indexSegKey.getId(),
                DBStatusType.ACTIVE,
                new IndexJob(indexSegKey));
    }
}
