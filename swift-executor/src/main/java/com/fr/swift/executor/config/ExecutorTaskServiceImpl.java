package com.fr.swift.executor.config;

import com.fr.swift.beans.annotation.SwiftBean;
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
@SwiftBean(name = "executorTaskService")
public class ExecutorTaskServiceImpl implements ExecutorTaskService {

    private ExecutorTaskService convertService;

    public ExecutorTaskServiceImpl() {
        this.convertService = new ExecutorTaskConvertService();
    }

    @Override
    public void save(ExecutorTask executorTask) throws SQLException {
        convertService.save(executorTask);
    }

    @Override
    public void update(ExecutorTask executorTask) throws SQLException {
        convertService.update(executorTask);
    }

    @Override
    public void batchSave(Set<ExecutorTask> executorTasks) throws SQLException {
        convertService.batchSave(executorTasks);
    }

    @Override
    public List<ExecutorTask> getActiveTasksBeforeTime(long time) {
        return convertService.getActiveTasksBeforeTime(time);
    }

    @Override
    public List<ExecutorTask> getRemoteActiveTasksBeforeTime(long time) {
        return convertService.getRemoteActiveTasksBeforeTime(time);
    }

    @Override
    public void delete(ExecutorTask executorTask) {
        convertService.delete(executorTask);
    }

    @Override
    public ExecutorTask get(String taskId) {
        return convertService.get(taskId);
    }
}