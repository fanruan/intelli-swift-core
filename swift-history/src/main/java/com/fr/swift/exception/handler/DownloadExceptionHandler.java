package com.fr.swift.exception.handler;

/**
 * Create by lifan on 2019-08-22 17:27
 */

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.DownloadExceptionContext;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentHelper;

import java.util.concurrent.TimeUnit;

/**
 * Create by lifan on 2019-08-22 15:54
 */
@SwiftBean
@RegisterExceptionHandler
public class DownloadExceptionHandler implements ExceptionHandler {

    private static int RETRY_TIMES = 5;

    private static long RETRY_INTERVAL = TimeUnit.MILLISECONDS.toMillis(4000);

    public static boolean success = false;

    @Override
    public void handleException(ExceptionInfo exceptionInfo) {
        // TODO: 2019-08-23 这里只处理了重试下载的策略 应该还可以寻找容易节点直传输
        if (SwiftRepositoryAccessibleTester.testAccessible()) {
            try {
                DownloadExceptionContext downloadExceptionContext = (DownloadExceptionContext) exceptionInfo.getContext();
                for (int i = 0; i < RETRY_TIMES; i++) {
                    success = true;
                    //重试下载
                    SegmentHelper.download(downloadExceptionContext.getSourceKey(), downloadExceptionContext.getUris(), downloadExceptionContext.isReplace());
                    if (success) {
                        break;
                    }
                    Thread.sleep(RETRY_INTERVAL);
                }
            } catch (Exception e) {
                success = false;
                SwiftLoggers.getLogger().info("ExceptionHandler Thread interrupted!");
            }
        } else {
            SwiftLoggers.getLogger().info("connection unAccessible!");
        }
    }

    @Override
    public boolean evaluate() {
        return success;
    }

    @Override
    public ExceptionInfo.Type getExceptionInfoType() {
        return ExceptionInfoType.DOWNLOAD_SEGMENT;
    }


}
