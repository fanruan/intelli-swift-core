package com.fr.swift.executor.task;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.StatusType;
import com.fr.swift.executor.type.TaskType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;

import java.lang.reflect.Constructor;

/**
 * This class created on 2019/2/11
 *
 * @author Lucifer
 */
@TaskType
public abstract class AbstractExecutorTask<T extends Job> implements ExecutorTask<T> {

    protected SourceKey sourceKey;
    protected boolean persistent;
    protected String taskId;
    protected long createTime;
    protected ExecutorTaskType executorTaskType;
    protected LockType lockType;
    protected String lockKey;
    protected DBStatusType dbStatusType;
    protected StatusType statusType;
    protected String taskContent;
    protected T job;

    //创建task
    protected AbstractExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType,
                                   LockType lockType, String lockKey, DBStatusType dbStatusType, T job) throws Exception {
        this.sourceKey = sourceKey;
        this.persistent = persistent;
        this.executorTaskType = executorTaskType;
        this.lockType = lockType;
        this.lockKey = lockKey;
        this.dbStatusType = dbStatusType;
        this.job = job;
        //job to taskContent
        this.statusType = StatusType.WAITING;
        this.createTime = System.currentTimeMillis();
        this.taskId = String.valueOf(createTime);

        this.taskContent = JsonBuilder.writeJsonString(job.serializedTag());
    }

    protected AbstractExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType, LockType lockType,
                                   String lockKey, DBStatusType dbStatusType, String taskId, long createTime, String taskContent) {
        this.sourceKey = sourceKey;
        this.persistent = persistent;
        this.executorTaskType = executorTaskType;
        this.lockType = lockType;
        this.lockKey = lockKey;
        this.dbStatusType = dbStatusType;
        this.statusType = StatusType.WAITING;
        this.createTime = createTime;
        this.taskId = taskId;
        this.taskContent = taskContent;
    }

    public static final Class TYPE = entityType();

    private static Class entityType() {
        try {
            return Class.forName("com.fr.swift.executor.config.SwiftExecutorTaskEntity");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public SourceKey getSourceKey() {
        return sourceKey;
    }

    @Override
    public String getTaskId() {
        return taskId;
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    @Override
    public ExecutorTaskType getExecutorTaskType() {
        return executorTaskType;
    }

    @Override
    public LockType getLockType() {
        return lockType;
    }

    @Override
    public String getLockKey() {
        return lockKey;
    }

    @Override
    public DBStatusType getDbStatusType() {
        return dbStatusType;
    }


    @Override
    public boolean isPersistent() {
        return persistent;
    }

    @Override
    public T getJob() {
        return job;
    }

    @Override
    public void setDbStatusType(DBStatusType dbStatusType) {
        this.dbStatusType = dbStatusType;
    }

    @Override
    public StatusType getStatusType() {
        return statusType;
    }

    @Override
    public void setStatusType(StatusType statusType) {
        this.statusType = statusType;
    }

    public String getTaskContent() {
        return taskContent;
    }

    @Override
    public String toString() {
        return "AbstractExecutorTask{" +
                "sourceKey=" + sourceKey +
                ", persistent=" + persistent +
                ", taskId='" + taskId + '\'' +
                ", createTime=" + createTime +
                ", executorTaskType=" + executorTaskType +
                ", lockType=" + lockType +
                ", lockKey='" + lockKey + '\'' +
                ", dbStatusType=" + dbStatusType +
                ", statusType=" + statusType +
                '}';
    }

    @Override
    public int hashCode() {
        int result = sourceKey != null ? sourceKey.hashCode() : 0;
        result = 31 * result + (persistent ? 0 : 1);
        result = 31 * result + (taskId != null ? taskId.hashCode() : 0);
        result = 31 * result + (executorTaskType != null ? executorTaskType.hashCode() : 0);
        result = 31 * result + (lockType != null ? lockType.hashCode() : 0);
        result = 31 * result + (lockKey != null ? lockKey.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractExecutorTask that = (AbstractExecutorTask) obj;
        if (sourceKey != null ? !sourceKey.equals(that.sourceKey) : that.sourceKey != null) {
            return false;
        }
        if (persistent != that.persistent) {
            return false;
        }
        if (taskId != null ? !taskId.equals(that.taskId) : that.taskId != null) {
            return false;
        }
        if (executorTaskType != that.executorTaskType) {
            return false;
        }
        if (lockType != that.lockType) {
            return false;
        }
        return lockKey != null ? lockKey.equals(that.lockKey) : that.lockKey == null;
    }

    @Override
    public Object convert() {
        try {
            Constructor constructor = TYPE.getDeclaredConstructor(ExecutorTask.class);
            return constructor.newInstance(this);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }
}
