package com.fr.swift.executor.task;

import com.fr.swift.executor.conflict.CustomizeTaskConflict;
import com.fr.swift.executor.conflict.MultiSkipList;
import com.fr.swift.executor.conflict.TaskConflict;
import com.fr.swift.executor.queue.ConsumeQueue;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.StatusType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * This class created on 2019/2/13
 *
 * @author Lucifer
 * @description TaskRouter的操作需要加全局锁，避免一边读一边写
 */
public class TaskRouter {

    private MultiSkipList<ExecutorTask> idleTasks;

    private static TaskRouter INSTANCE = new TaskRouter();

    private TaskConflict taskConflict = null;

    public static TaskRouter getInstance() {
        return INSTANCE;
    }

    private TaskRouter() {
        this.idleTasks = new MultiSkipList<>(new Comparator<ExecutorTask>() {
            @Override
            public int compare(ExecutorTask o1, ExecutorTask o2) {
                return Integer.compare(o1.getPriority(), o2.getPriority());
            }
        });
    }

    public synchronized boolean addTasks(List<ExecutorTask> taskList) {
        List<ExecutorTask> newTaskList = new ArrayList<>(taskList);
        Collections.sort(newTaskList, new TaskTimeComparator());
        this.idleTasks.addAll(newTaskList);
        return true;
    }

    public MultiSkipList<ExecutorTask> getIdleTasks() {
        if (taskConflict == null) {
            initTaskConflicts();
        }
        return idleTasks;
    }

    /**
     * 移除task并修改内存中执行状态
     *
     * @param task
     * @return
     */
    public synchronized boolean remove(ExecutorTask task) {
        task.setStatusType(StatusType.RUNNING);
        return idleTasks.remove(task);
    }

    public synchronized ExecutorTask pickExecutorTask(Lock lock) {
        MultiSkipList<ExecutorTask> idleTasks = TaskRouter.getInstance().getIdleTasks();
        ExecutorTask taskPicked = null;
        lock.lock();
        try {
            // 对 virtual seg 锁建索引
            taskConflict.initVirtualLocks(ConsumeQueue.getInstance().getTaskList());
            Iterator<ExecutorTask> iterator = idleTasks.iterator();
            while (iterator.hasNext()) {
                ExecutorTask curTask = iterator.next();
                if (isQualified(curTask)) {
                    taskPicked = curTask;
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
        // 完成冲突判定，删除 virtual seg 锁索引
        taskConflict.finishVirtualLocks();
        if (taskPicked != null && TaskRouter.getInstance().remove(taskPicked)) {
            return taskPicked;
        } else {
            return null;
        }
    }

    public synchronized void clear() {
        idleTasks.clear();
    }

    private boolean isQualified(ExecutorTask task) {
        try {
            List<ExecutorTask> taskList = ConsumeQueue.getInstance().getTaskList();
            return !taskConflict.isConflict(task, taskList);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.toString());
        }
        return false;


    }

    /**
     * 任务是否冲突
     *
     * @param runningTask
     * @param executorTask
     * @return true:冲突，不能同时执行   false:互斥，可以同时执行
     */
    private boolean isTasksConfilct(ExecutorTask runningTask, ExecutorTask executorTask) {
        //表名不同，直接return false
        if (Util.equals(runningTask.getSourceKey(), executorTask.getSourceKey())) {
            //有一个是NONE，则直接return false
            if (LockType.isNoneLock(runningTask) || LockType.isNoneLock(executorTask)) {
                return false;
            }
            switch (runningTask.getLockType()) {
                case TABLE://虚拟锁任务可以执行
                    return !LockType.isVirtualLock(executorTask);
                case REAL_SEG://虚拟锁任务可以执行；不是同一块的真实锁任务可以执行
                    if (LockType.isVirtualLock(executorTask)) {
                        return false;
                    }
                    return !LockType.isRealLock(executorTask) || LockType.isSameLockKey(runningTask, executorTask);
                case VIRTUAL_SEG://表锁、真实锁可以执行
                    if (LockType.isTableLock(executorTask)) {
                        return false;
                    }
                    return !LockType.isRealLock(executorTask);
                default:
                    return true;
            }
        } else {
            return false;
        }
    }

    /**
     * 根据task的createTime排序
     */
    private class TaskTimeComparator implements Comparator<ExecutorTask> {

        @Override
        public int compare(ExecutorTask task1, ExecutorTask task2) {
            return Long.compare(task1.getCreateTime(), task2.getCreateTime());
        }
    }

    /**
     * 读取配置文件对进行初始化
     */
    private void initTaskConflicts() {

//        InputStream swiftIn = null;
//        try {
//            SwiftLoggers.getLogger().info("read external swift.properties!");
//            swiftIn = new BufferedInputStream(new FileInputStream(("swift.properties")));
//        } catch (FileNotFoundException e) {
//            SwiftLoggers.getLogger().warn("Failed to read external swift.properties, read internal swift.properties instead!");
//            swiftIn = SwiftProperty.class.getClassLoader().getResourceAsStream("swift.properties");
//        }

        try {
            taskConflict = new CustomizeTaskConflict();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }


//        try {
//            String path = TaskRouter.class.getClassLoader().getResource("conflict-conf.json").getPath();
//            taskConflict = new CustomizeTaskConflict(path);
//        } catch (Exception e) {
//            // TODO 需要对这个错误进行处理，否则并发限制失效
//            SwiftLoggers.getLogger().error(e);
//        }
    }
}