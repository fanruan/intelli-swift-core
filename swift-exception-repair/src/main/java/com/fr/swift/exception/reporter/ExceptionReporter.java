package com.fr.swift.exception.reporter;

import com.fr.swift.SwiftContext;
import com.fr.swift.exception.ExceptionContext;
import com.fr.swift.exception.ExceptionInfoBean;
import com.fr.swift.exception.ExceptionInfoType;
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

    private static final ExceptionInfoService INFO_SERVICE = SwiftContext.get().getBean(ExceptionInfoService.class);

    /**
     * 包装，持久化异常，添加异常到队列中
     *
     * @param context
     */
    public static void report(ExceptionContext context) {
        ExceptionInfoBean bean = new ExceptionInfoBean.Builder()
                .setNowAndHere()
                .setType(ExceptionInfoType.UPLOAD_SEGMENT)
                .setContext(context).build();
        if (INFO_SERVICE.existsException(bean)) {
            SwiftLoggers.getLogger().info("Exception exists!");
            return;
        }
        INFO_SERVICE.maintain(bean);
        if (!SlaveExceptionInfoQueue.getInstance().offer(bean)) {
            SwiftLoggers.getLogger().warn("Add into SlaveExceptionInfoQueue Failed");
        }
    }
}