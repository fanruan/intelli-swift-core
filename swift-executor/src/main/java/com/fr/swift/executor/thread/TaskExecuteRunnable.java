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
                executorJob.run();
                boolean success;
                try {
                    executorJob.get();
                    success = true;
                    if (executorTask.isPersistent()) {
                        executorTask.setDbStatusType(DBStatusType.SUCCESS);
                        executorTaskService.saveOrUpdate(executorTask);
                    }
//                    executorTaskService.deleteTask(executorTask);
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                    success = false;
                    executorTask.setDbStatusType(DBStatusType.FAILED);
                    executorTaskService.saveOrUpdate(executorTask);
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
