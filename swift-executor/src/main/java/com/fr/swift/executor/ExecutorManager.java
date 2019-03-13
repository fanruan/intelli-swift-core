package com.fr.swift.executor;

import com.fr.swift.executor.dispatcher.TaskDispatcher;
import com.fr.swift.executor.queue.DBQueue;
import com.fr.swift.executor.queue.MemoryQueue;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.TaskRouter;

import java.util.List;

/**
 * This class created on 2019/2/11
 *
 * @author Lucifer
 * @description
 */
public class ExecutorManager {

    private static ExecutorManager INSTANCE = new ExecutorManager();

    public static ExecutorManager getInstance() {
        return INSTANCE;
    }

    private ExecutorManager() {
        TaskDispatcher.getInstance();
    }

    public boolean pullMemTask() {
        List<ExecutorTask> memTasks = MemoryQueue.getInstance().pullBeforeTime(System.nanoTime());
        return addTasks(memTasks);
    }

    public boolean pullDBTask() {
        List<ExecutorTask> dbTasks = DBQueue.getInstance().pullAll();
        return addTasks(dbTasks);
    }

    private boolean addTasks(List<ExecutorTask> taskList) {
        if (!taskList.isEmpty()) {
            return TaskRouter.getInstance().addTasks(taskList);
        } else {
            return false;
        }
    }
}
