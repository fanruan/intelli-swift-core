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

    List<Object[]> getActiveTasksGroupByCluster(long time);

    void delete(final ExecutorTask executorTask);

    ExecutorTask get(String taskId);
}