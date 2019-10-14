package com.fr.swift.exception;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 10/14/2019
 */
public class RetryUploadSegScheduledWorker {

    private long delay = 0L;

    //每10分钟重试一次
    private long period = 1000 * 60 * 10;

    private final CountDownLatch latch = new CountDownLatch(1);

    public void work(ExceptionInfo info) {
        ScheduledExecutorService service = SwiftExecutors.newSingleThreadScheduledExecutor(new PoolThreadFactory(UploadSegExecutor.class));
        UploadSegExecutor executor = new UploadSegExecutor(info, latch);
        service.scheduleAtFixedRate(executor, delay, period, TimeUnit.MILLISECONDS);
        try {
            latch.await();
        } catch (InterruptedException e) {
            SwiftLoggers.getLogger().error(e);
        } finally {
            service.shutdownNow();
        }
    }
}