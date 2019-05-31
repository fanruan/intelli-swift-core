package com.fr.swift.executor;

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
    }

    public boolean pullMemTask() {
        List<ExecutorTask> memTasks = MemoryQueue.getInstance().pullBeforeTime(System.currentTimeMillis());
        return addTasks(memTasks);
    }

    public boolean pullDBTask() {
        List<ExecutorTask> dbTasks = DBQueue.getInstance().pullAll();
        return addTasks(dbTasks);
    }

    // TODO: 2019/3/11 by lucifer 清理线程中正在执行的任务
    public void clearTasks() {
        MemoryQueue.getInstance().clear();
        TaskRouter.getInstance().clear();
    }

    private boolean addTasks(List<ExecutorTask> taskList) {
        if (!taskList.isEmpty()) {
            return TaskRouter.getInstance().addTasks(taskList);
        } else {
            return false;
        }
    }
}
