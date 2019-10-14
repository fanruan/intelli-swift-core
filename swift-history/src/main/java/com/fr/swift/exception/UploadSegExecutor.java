package com.fr.swift.exception;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.exception.inspect.ComponentHealthCheck;
import com.fr.swift.exception.inspect.SwiftRepositoryHealthInspector;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.SwiftUploadService;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 10/14/2019
 */
public class UploadSegExecutor implements Runnable {

    private ExceptionInfo info;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final CountDownLatch latch;

    private final long firstRetryTime;

    //重试时间超过24小时则不再重试
    private final long longestRetryTime;

    @SwiftAutoWired
    private SwiftUploadService uploadService;

    private ComponentHealthCheck repositoryChecker = new ComponentHealthCheck(SwiftRepositoryHealthInspector.getInstance(), 30000);

    UploadSegExecutor(ExceptionInfo info, CountDownLatch latch) {
        this.info = info;
        this.latch = latch;
        this.firstRetryTime = System.currentTimeMillis();
        this.longestRetryTime = firstRetryTime + 1000 * 60 * 60 * 24;
    }

    @Override
    public void run() {
        if (System.currentTimeMillis() > longestRetryTime) {
            latch.countDown();
            SwiftLoggers.getLogger().info("Failed to Access Repository for More Than 24 Hours, Stop Checking Repository !");
        } else if (repositoryChecker.isHealthy()) {
            SegmentKey key = ((UploadExceptionContext) info.getContext()).getSegmentKey();
            if (!((UploadExceptionContext) info.getContext()).isAllShow()) {
                uploadService.upload(Collections.singleton(key));
            } else {
                uploadService.uploadAllShow(Collections.singleton(key));
            }
            SwiftLoggers.getLogger().info("Retry Upload Seg Succeed at %s", formatter.format(System.currentTimeMillis()));
            latch.countDown();
        }
        SwiftLoggers.getLogger().info("Repository Check Failed at %s", formatter.format(System.currentTimeMillis()));
    }
}
