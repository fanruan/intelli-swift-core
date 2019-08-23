package com.fr.swift.exception.handler;

/**
 * Create by lifan on 2019-08-22 17:27
 */

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.DownloadExceptionContext;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.manager.SwiftRepositoryManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Create by lifan on 2019-08-22 15:54
 */
@SwiftBean
@RegisterExceptionHandler
public class DownloadExceptionHandler implements ExceptionHandler {

    private static final int RETRY_TIMES = 5;

    private static final int RETRY_INTERVAL = 4;

    @Override
    public boolean handleException(ExceptionInfo exceptionInfo) {
        // TODO: 2019-08-23 这里只处理了重试下载的策略 应该还可以寻找冗余节点直传输
        if (SwiftRepositoryAccessibleTester.testAccessible()) {
            try {
                DownloadExceptionContext downloadExceptionContext = (DownloadExceptionContext) exceptionInfo.getContext();
                for (int i = 0; i < RETRY_TIMES; i++) {
                    if (retryDownload(downloadExceptionContext)) {
                        return true;
                    } else {
                        TimeUnit.SECONDS.sleep(RETRY_INTERVAL);
                    }
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().info("ExceptionHandler Thread interrupted!", e);
            }
        } else {
            SwiftLoggers.getLogger().info("connection unAccessible!");
        }
        return false;
    }

    private boolean retryDownload(DownloadExceptionContext downloadExceptionContext) {
        //重试下载
        try {
            SwiftRepositoryManager.getManager().currentRepo().copyFromRemote(downloadExceptionContext.getRemotePath(), downloadExceptionContext.getCubePath());
            return true;
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }

    @Override
    public ExceptionInfo.Type getExceptionInfoType() {
        return ExceptionInfoType.DOWNLOAD_SEGMENT;
    }


}
