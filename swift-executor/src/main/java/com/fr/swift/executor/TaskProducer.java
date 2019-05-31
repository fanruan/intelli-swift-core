package com.fr.swift.executor;

import com.fr.swift.executor.exception.NotDBTaskException;
import com.fr.swift.executor.queue.DBQueue;
import com.fr.swift.executor.queue.MemoryQueue;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.log.SwiftLoggers;

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
    public static boolean produceTasks(Set<ExecutorTask> executorTasks) throws SQLException, NotDBTaskException {
        for (ExecutorTask task : executorTasks) {
            if (!task.isPersistent()) {
                throw new NotDBTaskException(task);
            }
        }
        try {
            DBQueue.getInstance().put(executorTasks);
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error("tasks {} insert db failed! ", executorTasks, e);
        }
        for (ExecutorTask executorTask : executorTasks) {
            MemoryQueue.getInstance().offer(executorTask);
        }
        return true;
    }

    public static boolean produceTask(ExecutorTask executorTask) {
        if (executorTask.isPersistent()) {
            try {
                DBQueue.getInstance().put(executorTask);
            } catch (SQLException e) {
                SwiftLoggers.getLogger().error("task {} insert db failed! ", executorTask, e);
            }
        }
        return MemoryQueue.getInstance().offer(executorTask);
    }
}
