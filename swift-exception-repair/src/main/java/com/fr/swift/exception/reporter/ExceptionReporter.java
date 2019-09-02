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

    /**
     * 包装，持久化异常，添加异常到队列中
     *
     * @param context
     */
    public static void report(ExceptionContext context, ExceptionInfoType type) {
        ExceptionInfoBean bean = new ExceptionInfoBean.Builder()
                .setNowAndHere()
                .setType(type)
                .setContext(context).build();
        ExceptionInfoService infoService = SwiftContext.get().getBean(ExceptionInfoService.class);
        if (infoService.existsException(bean)) {
            SwiftLoggers.getLogger().info("Exception info {} exists!", bean);
            return;
        }
        infoService.maintain(bean);
        if (!SlaveExceptionInfoQueue.getInstance().offer(bean)) {
            SwiftLoggers.getLogger().warn("Add into SlaveExceptionInfoQueue Failed, {}", bean);
        }
    }
}