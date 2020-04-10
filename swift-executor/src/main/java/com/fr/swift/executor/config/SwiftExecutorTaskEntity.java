package com.fr.swift.executor.config;

import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.ExecutorTypeContainer;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.TaskType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.source.SourceKey;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class created on 2019/2/26
 *
 * @author Lucifer
 * @description
 */
@Entity
@Table(name = "fine_swift_executor_tasks")
public class SwiftExecutorTaskEntity implements Serializable {

    private static final long serialVersionUID = -7333801707856105168L;

    private final static String ID_SEPARATOR = ":";

    @Column(name = "executorTaskType")
    protected String executorTaskType;

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
    @Enumerated(EnumType.STRING)
    protected LockType lockType;

    @Column(name = "lockKey")
    protected String lockKey;

    @Column(name = "priority", columnDefinition = "int default 0")
    protected int priority;

    @Column(name = "cause", length = 4000)
    protected String cause;

    @Column(name = "finishTime", columnDefinition = "bigint default 0")
    protected long finishTime;

    @Column(name = "startTime", columnDefinition = "bigint default 0")
    protected long startTime;

    @Column(name = "dbStatusType")
    @Enumerated(EnumType.STRING)
    protected DBStatusType dbStatusType;

    @Column(name = "clusterId")
    protected String clusterId;
    @Transient
    private ExecutorTaskType taskType;

    private SwiftExecutorTaskEntity() {
    }

    private SwiftExecutorTaskEntity(ExecutorTask task) {
        this.clusterId = SwiftProperty.get().getMachineId();
        this.taskId = task.getTaskId();
        this.sourceKey = task.getSourceKey().getId();
        this.createTime = task.getCreateTime();
        this.taskType = task.getExecutorTaskType();
        this.executorTaskType = task.getExecutorTaskType().name();
        this.lockType = task.getLockType();
        this.lockKey = task.getLockKey();
        this.dbStatusType = task.getDbStatusType();
        this.id = clusterId + ID_SEPARATOR + taskId;
        this.taskContent = task.getTaskContent();
        this.priority = task.getPriority();
        this.cause = task.getCause();
        this.finishTime = task.getFinishTime();
        this.startTime = task.getStartTime();
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

    public ExecutorTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(ExecutorTaskType taskType) {
        this.taskType = taskType;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public ExecutorTask convert() {
        try {
            Class<? extends ExecutorTask> clazz = ExecutorTypeContainer.getInstance().getClassByType(this.executorTaskType);

            TaskType taskTypeAnnotation = clazz.getAnnotation(TaskType.class);
            taskType = (ExecutorTaskType) Enum.valueOf(taskTypeAnnotation.type(), executorTaskType);
            Constructor constructor = clazz.getDeclaredConstructor(SourceKey.class, boolean.class, ExecutorTaskType.class, LockType.class,
                    String.class, DBStatusType.class, String.class, long.class, String.class, int.class);

            return (ExecutorTask) constructor.newInstance(new SourceKey(this.sourceKey), true, this.taskType, this.lockType,
                    this.lockKey, this.dbStatusType, this.taskId, this.createTime, this.taskContent, this.priority);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }

    public static SwiftExecutorTaskEntity convertEntity(ExecutorTask executorTask) {
        return new SwiftExecutorTaskEntity(executorTask);
    }

    public static Collection<SwiftExecutorTaskEntity> convertEntities(Collection<ExecutorTask> executorTasks) {
        Set<SwiftExecutorTaskEntity> entities = new HashSet<>();
        for (ExecutorTask executorTask : executorTasks) {
            entities.add(convertEntity(executorTask));
        }
        return entities;
    }
}