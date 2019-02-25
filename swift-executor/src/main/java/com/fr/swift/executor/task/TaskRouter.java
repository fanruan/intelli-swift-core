package com.fr.swift.executor.task;

import com.fr.swift.executor.type.StatusType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class created on 2019/2/13
 *
 * @author Lucifer
 * @description TaskRouter的操作需要加全局锁，避免一边读一边写
 */
public class TaskRouter {

    private List<ExecutorTask> idleTasks;

    private static TaskRouter INSTANCE = new TaskRouter();

    public static TaskRouter getInstance() {
        return INSTANCE;
    }

    private TaskRouter() {
        this.idleTasks = new ArrayList<ExecutorTask>();
    }

    public boolean addTasks(List<ExecutorTask> taskList) {
        List<ExecutorTask> newTaskList = new ArrayList<ExecutorTask>(taskList);
        Collections.sort(newTaskList, new TaskTimeComparator());
        this.idleTasks.addAll(newTaskList);
        return true;
    }

    public List<ExecutorTask> getIdleTasks() {
        return idleTasks;
    }

    /**
     * 移除task并修改内存中执行状态
     *
     * @param task
     * @return
     */
    public boolean remove(ExecutorTask task) {
        task.setStatusType(StatusType.RUNNING);
        return idleTasks.remove(task);
    }

    /**
     * 根据task的createTime排序
     */
    private class TaskTimeComparator implements Comparator<ExecutorTask> {

        @Override
        public int compare(ExecutorTask task1, ExecutorTask task2) {
            if (task1.getCreateTime() >= task2.getCreateTime()) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}