package com.fr.swift.executor.config;

import com.fr.swift.annotation.persistence.Column;
import com.fr.swift.annotation.persistence.Entity;
import com.fr.swift.annotation.persistence.Enumerated;
import com.fr.swift.annotation.persistence.Id;
import com.fr.swift.annotation.persistence.Table;
import com.fr.swift.annotation.persistence.Transient;
import com.fr.swift.converter.ObjectConverter;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.ExecutorTypeContainer;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.source.SourceKey;

import java.io.Serializable;
import java.lang.reflect.Constructor;

/**
 * This class created on 2019/2/26
 *
 * @author Lucifer
 * @description
 */
@Entity
@Table(name = "fine_swift_executor_tasks")
public class SwiftExecutorTaskEntity implements Serializable, ObjectConverter<ExecutorTask> {

    private static final long serialVersionUID = -7333801707856105168L;

    private final static String ID_SEPARATOR = ":";

    @Column(name = "executorTaskType")
    protected String taskType;

    @Column(name = "taskId")
    protected String taskId;

    @Column(name = "sourceKey")
    protected String sourceKey;

    @Column(name = "createTime")
    protected long createTime;
    @Column(name = "taskContent", length = 4000)
    protected String taskContent;
    @Id
    private String id;

    @Column(name = "lockType")
    @Enumerated(Enumerated.EnumType.STRING)
    protected LockType lockType;

    @Column(name = "lockKey")
    protected String lockKey;

    @Column(name = "dbStatusType")
    @Enumerated(Enumerated.EnumType.STRING)
    protected DBStatusType dbStatusType;

    @Column(name = "clusterId")
    protected String clusterId;
    @Transient
    private ExecutorTaskType executorTaskType;

    public SwiftExecutorTaskEntity() {
    }

    public SwiftExecutorTaskEntity(ExecutorTask task) {
        this.clusterId = SwiftProperty.getProperty().getClusterId();
        this.taskId = task.getTaskId();
        this.sourceKey = task.getSourceKey().getId();
        this.createTime = task.getCreateTime();
        this.executorTaskType = task.getExecutorTaskType();
        this.taskType = task.getExecutorTaskType().name();
        this.lockType = task.getLockType();
        this.lockKey = task.getLockKey();
        this.dbStatusType = task.getDbStatusType();
        this.id = clusterId + ID_SEPARATOR + taskId;
        this.taskContent = task.getTaskContent();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public ExecutorTaskType getExecutorTaskType() {
        return executorTaskType;
    }

    public void setExecutorTaskType(ExecutorTaskType executorTaskType) {
        this.executorTaskType = executorTaskType;
    }

    public LockType getLockType() {
        return lockType;
    }

    public void setLockType(LockType lockType) {
        this.lockType = lockType;
    }

    public void setDbStatusType(DBStatusType dbStatusType) {
        this.dbStatusType = dbStatusType;
    }

    public DBStatusType getDbStatusType() {
        return dbStatusType;
    }

    public String getLockKey() {
        return lockKey;
    }

    public void setLockKey(String lockKey) {
        this.lockKey = lockKey;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }


    @Override
    public ExecutorTask convert() {
        try {
            Class<? extends ExecutorTask> clazz = ExecutorTypeContainer.getInstance().getClassByType(this.taskType);

            Constructor constructor = clazz.getDeclaredConstructor(SourceKey.class, boolean.class, ExecutorTaskType.class, LockType.class,
                    String.class, DBStatusType.class, String.class, long.class, String.class);

            return (ExecutorTask) constructor.newInstance(new SourceKey(this.sourceKey), true, this.executorTaskType, this.lockType,
                    this.lockKey, this.dbStatusType, this.taskId, this.createTime, this.taskContent);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }
}