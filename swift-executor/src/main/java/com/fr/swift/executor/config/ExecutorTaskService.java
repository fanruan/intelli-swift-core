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

    boolean saveOrUpdate(final ExecutorTask executorTask) throws SQLException;

    boolean batchSaveOrUpdate(final Set<ExecutorTask> executorTasks) throws SQLException;

    List<ExecutorTask> getActiveTasksBeforeTime(long time);

    List<ExecutorTask> getRemoteActiveTasksBeforeTime(long time);

    boolean deleteTask(final ExecutorTask executorTask);

    ExecutorTask getExecutorTask(String taskId);

    List<ExecutorTask> getSuccessTasksBeforeTime(long time);
}