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
 * This class created on 2019/5/9
 *
 * @author Lucifer
 * @description
 */
@TaskType(type = CloudTaskType.class)
public class TreasureUploadTask extends AbstractExecutorTask<Job> {

    public TreasureUploadTask(TreasureBean treasureBean) throws Exception {
        super(new SourceKey(treasureBean.getTopic()),
                true,
                CloudTaskType.TREASURE_UPLOAD,
                LockType.TABLE,
                treasureBean.getTopic(),
                DBStatusType.ACTIVE,
                new TreasureUploadJob(treasureBean));
    }

    public TreasureUploadTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType, LockType lockType,
                              String lockKey, DBStatusType dbStatusType, String taskId, long createTime, String taskContent) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent);

        TreasureBean treasureBean = JsonBuilder.readValue(taskContent, TreasureBean.class);
        this.job = new TreasureUploadJob(treasureBean);
    }
}
