package com.fr.swift.executor.thread;

import com.fr.swift.SwiftContext;
import com.fr.swift.executor.config.ExecutorTaskService;
import com.fr.swift.executor.queue.ConsumeQueue;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.ExecutorTypeContainer;
import com.fr.swift.executor.task.job.ExecutorJob;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.Job.JobListener;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.log.SwiftLoggers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * This class created on 2019/2/14
 *
 * @author Lucifer
 * @description
 */
public class TaskExecuteRunnable implements Runnable {

    private ExecutorTask executorTask;

    private final String threadName;

    private List<ExecutorTaskType> threadExecutorTypes;

    private Lock lock;

    private Condition freeCondition;

    private Thread dispachThread;

    private ExecutorTaskService executorTaskService = SwiftContext.get().getBean(ExecutorTaskService.class);

    public TaskExecuteRunnable(Thread dispachThread, String threadName, Lock lock, Condition condition, ExecutorTaskType... types) {
        this.dispachThread = dispachThread;
        this.threadName = threadName;
        this.lock = lock;
        this.freeCondition = condition;
        if (types.length == 0) {
            threadExecutorTypes = ExecutorTypeContainer.getInstance().getExecutorTaskTypeList();
        } else {
            threadExecutorTypes = Arrays.asList(types);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                executorTask = ConsumeQueue.getInstance().take();
                Job job = executorTask.getJob();
                JobListener jobListener = job.getJobListener();
                ExecutorJob executorJob = new ExecutorJob(job);
                executorTask.setStartTime(System.currentTimeMillis());
                executorJob.run();
                boolean success;
                try {
                    Object result = executorJob.get();
                    success = true;
                    if (executorTask.isPersistent()) {
                        executorTask.setDbStatusType(DBStatusType.SUCCESS);
                        executorTask.setFinishTime(System.currentTimeMillis());
                        executorTask.setCause(result.toString());
                        executorTaskService.saveOrUpdate(executorTask);
                    }
//                    executorTaskService.deleteTask(executorTask);
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                    success = false;
                    executorTask.setDbStatusType(DBStatusType.FAILED);
                    StringBuilder cause = new StringBuilder();
                    try {
                        cause.append("[").append(e.getMessage()).append("]");
                        Throwable t = e.getCause();
                        // 只记录10层， 长度截断4000
                        int count = 10;
                        while (t != null && count > 0) {
                            cause.append("[").append(t.getMessage()).append("]");
                            t = t.getCause();
                            count--;
                        }
                    } catch (Exception ee) {
                        cause.append("failed in collect message: ");
                        cause.append(ee.getMessage());
                    }
                    if (executorTask.isPersistent()) {
                        executorTask.setCause(cause.toString().substring(0, Math.min(cause.length(), 4000)));
                        executorTask.setFinishTime(System.currentTimeMillis());
                        executorTaskService.saveOrUpdate(executorTask);
                    }
                }
                if (jobListener != null) {
                    jobListener.onDone(success);
                }
            } catch (InterruptedException e) {
                SwiftLoggers.getLogger().warn("Thread:[{}] catch InterruptedException! Exit!", threadName, e);
                break;
            } catch (Throwable t) {
                SwiftLoggers.getLogger().error(t);
            } finally {
                lock.lock();
                try {
                    ConsumeQueue.getInstance().removeTask(executorTask);
                    executorTask = null;
                    freeCondition.signal();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public ExecutorTask getExecutorTask() {
        return executorTask;
    }

    public String getThreadName() {
        return threadName;
    }

    public boolean isIdle() {
        return executorTask == null;
    }

    /**
     * 任务类型是否与线程类型匹配
     *
     * @param type
     * @return
     */
    public boolean isMatch(ExecutorTaskType type) {
        return threadExecutorTypes.contains(type);
    }

    @Override
    public String toString() {
        // TODO: 2019/2/19
        StringBuilder threadExecutorTypeString = new StringBuilder();
        for (ExecutorTaskType threadExecutorType : threadExecutorTypes) {
            threadExecutorTypeString.append("[" + threadExecutorType.name() + "]");
        }
        return "TaskExecuteRunnable{" +
                "threadName='" + threadName + '\'' +
                ", threadExecutorTypes=" + threadExecutorTypeString +
                '}';
    }
}
