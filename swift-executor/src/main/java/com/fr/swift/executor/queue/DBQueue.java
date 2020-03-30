package com.fr.swift.executor.queue;

import com.fr.swift.SwiftContext;
import com.fr.swift.executor.config.ExecutorTaskService;
import com.fr.swift.executor.task.ExecutorTask;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2019/2/11
 *
 * @author Lucifer
 * @description 代理的数据库队列
 */
public final class DBQueue {

    private static DBQueue INSTANCE = new DBQueue();

    private ExecutorTaskService executorTaskService = SwiftContext.get().getBean(ExecutorTaskService.class);

    private long maxCreatetime = 0L;

    public static DBQueue getInstance() {
        return INSTANCE;
    }

    private DBQueue() {
    }

    public void put(Set<ExecutorTask> tasks) throws SQLException {
        executorTaskService.batchSave(tasks);
    }

    public void put(ExecutorTask task) throws SQLException {
        executorTaskService.save(task);
    }

    public synchronized List<ExecutorTask> pullAll() {
        List<ExecutorTask> executorTaskList = executorTaskService.getActiveTasksBeforeTime(maxCreatetime);
        return executorTaskList;
    }
}