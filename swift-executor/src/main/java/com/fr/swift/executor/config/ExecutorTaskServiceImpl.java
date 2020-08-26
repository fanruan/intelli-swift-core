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
    public List<ExecutorTask> getActiveDeleteTasksBeforeTime(long time) {
        return convertService.getActiveDeleteTasksBeforeTime(time);
    }

    @Override
    public List<Object[]> getActiveTasksGroupByCluster(long time) {
        return convertService.getActiveTasksGroupByCluster(time);
    }

    @Override
    public List<Object[]> getMaxtimeByContent(String... likes) {
        return convertService.getMaxtimeByContent(likes);
    }

    @Override
    public SwiftExecutorTaskEntity getRepeatTaskByTime(long createTime, String... likes) {
        return convertService.getRepeatTaskByTime(createTime, likes);
    }

    @Override
    public List<SwiftExecutorTaskEntity> getRepeatTasksByTime(long beginTime, long endTime, String... likes) {
        return convertService.getRepeatTasksByTime(beginTime, endTime, likes);
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