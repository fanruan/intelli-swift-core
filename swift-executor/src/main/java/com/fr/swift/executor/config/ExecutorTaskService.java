package com.fr.swift.executor.config;

import com.fr.swift.executor.task.ExecutorTask;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2019/2/26
 *
 * @author Lucifer
 * @description
 */
public interface ExecutorTaskService {

    void save(final ExecutorTask executorTask) throws SQLException;

    void update(final ExecutorTask executorTask) throws SQLException;

    void batchSave(final Set<ExecutorTask> executorTasks) throws SQLException;

    List<ExecutorTask> getActiveTasksBeforeTime(long time);

    List<ExecutorTask> getActiveDeleteTasksBeforeTime(long time);

    /**
     * Object[3]=clusterId,executorTaskType,count(*)
     *
     * @param time
     * @return
     */
    List<Object[]> getActiveTasksGroupByCluster(long time);

    /**
     * Object[2] =dbStatusType,max(s.createTime)
     *
     * @param likes
     * @return
     */
    List<Object[]> getMaxtimeByContent(String... likes);

    SwiftExecutorTaskEntity getRepeatTaskByTime(long createTime, String... likes);

    List<SwiftExecutorTaskEntity> getRepeatTasksByTime(long beginTime, long endTime, String... likes);

    void delete(final ExecutorTask executorTask);

    ExecutorTask get(String taskId);

    List<TaskBalanceEntity> getTaskBalances();
}