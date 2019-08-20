package com.fr.swift.exception.reporter;

import com.fr.swift.SwiftContext;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.queue.SlaveExceptionInfoQueue;
import com.fr.swift.exception.service.ExceptionInfoService;
import com.fr.swift.log.SwiftLoggers;

/**
 * @author Marvin
 * @date 8/13/2019
 * @description
 * @since swift 1.1
 */
public class ExceptionReporter {

    /**
     * 包装，持久化异常，添加异常到队列中
     *
     * @param exceptionInfo
     */
    public static void report(ExceptionInfo exceptionInfo) {
        maintain(exceptionInfo);
        if (!SlaveExceptionInfoQueue.getInstance().offer(exceptionInfo)) {
            SwiftLoggers.getLogger().warn("can`t add into SlaveExceptionInfoQueue");
        }
    }

    private static void maintain(ExceptionInfo info) {
        SwiftContext.get().getBean(ExceptionInfoService.class).maintain(info);
    }
}