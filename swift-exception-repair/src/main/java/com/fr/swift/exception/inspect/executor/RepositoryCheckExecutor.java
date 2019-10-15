package com.fr.swift.exception.inspect.executor;

import com.fr.swift.exception.inspect.ComponentHealthCheck;
import com.fr.swift.exception.inspect.SwiftRepositoryHealthInspector;
import com.fr.swift.exception.inspect.executor.retry.RetryTaskConsumer;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 10/14/2019
 */
public class RepositoryCheckExecutor implements Runnable {

    ExecutorService taskConsumerService = SwiftExecutors.newSingleThreadExecutor(new PoolThreadFactory(RetryTaskConsumer.class));
    long timeOut = 8L;
    private ComponentHealthCheck repositoryChecker = new ComponentHealthCheck(SwiftRepositoryHealthInspector.getInstance(), 30000);

    @Override
    public void run() {
        if (repositoryChecker.isHealthy()) {
            Future<Boolean> future = taskConsumerService.submit(new RetryTaskConsumer());
            try {
                //重试任务时间过长可能是线程被阻塞，取消阻塞线程
                future.get(timeOut, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                SwiftLoggers.getLogger().error(e);
            } catch (ExecutionException e) {
                SwiftLoggers.getLogger().error(e);
            } catch (TimeoutException e) {
                future.cancel(true);
                SwiftLoggers.getLogger().info("Retry Task Timeout, Cancel Retry Task !");
            }
        }
    }
}
