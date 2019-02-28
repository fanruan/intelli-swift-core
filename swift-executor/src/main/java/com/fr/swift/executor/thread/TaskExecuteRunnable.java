package com.fr.swift.executor.thread;

import com.fr.swift.executor.queue.ConsumeQueue;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.job.ExecutorJob;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.Job.JobListener;
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

    private Thread dispachTthread;

    public TaskExecuteRunnable(Thread dispachTthread, String threadName, Lock lock, Condition condition, ExecutorTaskType... types) {
        this.dispachTthread = dispachTthread;
        this.threadName = threadName;
        this.lock = lock;
        this.freeCondition = condition;
        if (types.length == 0) {
            threadExecutorTypes = Arrays.asList(ExecutorTaskType.values());
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
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                    success = false;
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
                    dispachTthread.interrupt();
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
