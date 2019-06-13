package com.fr.swift.cloud.task;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.cloud.bean.TreasureBean;
import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.TaskType;
import com.fr.swift.source.SourceKey;

/**
 * This class created on 2019/6/12
 *
 * @author Lucifer
 * @description
 */
@TaskType(type = CloudTaskType.class)
public class TreasureAnalysisTask extends AbstractExecutorTask<Job> {

    public TreasureAnalysisTask(TreasureBean treasureBean) throws Exception {
        super(new SourceKey(treasureBean.getTopic() + CloudTaskType.TREASURE_ANALYSIS.name()),
                true,
                CloudTaskType.TREASURE_ANALYSIS,
                LockType.TABLE,
                treasureBean.getTopic() + CloudTaskType.TREASURE_ANALYSIS.name(),
                DBStatusType.ACTIVE,
                new TreasureAnalysisJob(treasureBean));
    }

    public TreasureAnalysisTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType, LockType lockType,
                                String lockKey, DBStatusType dbStatusType, String taskId, long createTime, String taskContent) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent);

        TreasureBean treasureBean = JsonBuilder.readValue(taskContent, TreasureBean.class);
        this.job = new TreasureAnalysisJob(treasureBean);
    }
}