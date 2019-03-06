package com.fr.swift.executor;

import com.fr.swift.executor.exception.NotDBTaskExecption;
import com.fr.swift.executor.queue.DBQueue;
import com.fr.swift.executor.queue.MemoryQueue;
import com.fr.swift.executor.task.ExecutorTask;

import java.sql.SQLException;
import java.util.Set;

/**
 * This class created on 2019/2/21
 *
 * @author Lucifer
 * @description
 */
public class TaskProducer {

    /**
     * db task批量生产
     *
     * @param executorTasks
     * @return
     * @throws SQLException
     */
    public static boolean produceTasks(Set<ExecutorTask> executorTasks) throws SQLException, NotDBTaskExecption {
        for (ExecutorTask task : executorTasks) {
            if (!task.isPersistent()) {
                throw new NotDBTaskExecption(task);
            }
        }
        return DBQueue.getInstance().put(executorTasks);
    }

    public static boolean produceTask(ExecutorTask executorTask) throws SQLException {
        if (executorTask.isPersistent()) {
            return DBQueue.getInstance().put(executorTask);
        } else {
            return MemoryQueue.getInstance().offer(executorTask);
        }
    }
}
