package com.fr.swift.executor;

import com.fr.swift.executor.queue.DBQueue;
import com.fr.swift.executor.queue.MemoryQueue;
import com.fr.swift.executor.task.ExecutorTask;

import java.sql.SQLException;

/**
 * This class created on 2019/2/21
 *
 * @author Lucifer
 * @description
 */
public class TaskProducer {

    public static boolean produceTask(ExecutorTask executorTask) throws SQLException {
        if (executorTask.isPersistent()) {
            return DBQueue.getInstance().put(executorTask);
        } else {
            return MemoryQueue.getInstance().offer(executorTask);
        }
    }
}
