package com.fr.swift.executor;

import com.fr.swift.executor.queue.DBQueue;
import com.fr.swift.executor.queue.MemoryQueue;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.TaskRouter;

import java.util.ArrayList;
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
    }

    public boolean pull() {
        List<ExecutorTask> dbTasks = DBQueue.getInstance().pullAll();
        List<ExecutorTask> memTasks = MemoryQueue.getInstance().pullBeforeTime(System.currentTimeMillis());
        List<ExecutorTask> taskList = new ArrayList<ExecutorTask>(dbTasks.size() + memTasks.size());
        taskList.addAll(dbTasks);
        taskList.addAll(memTasks);
        if (!taskList.isEmpty()) {
            synchronized (TaskRouter.class) {
                TaskRouter.getInstance().addTasks(taskList);
            }
            return true;
        } else {
            return false;
        }
    }
}
