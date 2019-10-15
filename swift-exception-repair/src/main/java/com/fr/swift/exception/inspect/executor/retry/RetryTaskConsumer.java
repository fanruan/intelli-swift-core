package com.fr.swift.exception.inspect.executor.retry;

import com.fr.swift.log.SwiftLoggers;

import java.util.concurrent.Callable;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 10/15/2019
 */
public class RetryTaskConsumer implements Callable<Boolean> {

    private RetryTask task;

    @Override
    public Boolean call() throws Exception {
        //最多执行100个重试任务，防止一次调度重试太多任务影响到下次调度
        //多余的任务和在RetryTaskConsumer运行期间加入重试队列的任务放到下一次调度中执行
        int taskCount = Math.min(RepositoryRetryTaskQueue.getInstance().getTaskCount(), 100);
        for (int i = 0; i < taskCount; i++) {
            try {
                task = RepositoryRetryTaskQueue.getInstance().take();
                task.retry();
            } catch (InterruptedException e) {
                SwiftLoggers.getLogger().error(e);
                return false;
            }
        }
        return true;

    }
}
