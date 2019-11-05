package com.fr.swift.exception.reporter;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.ExceptionContext;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoBean;
import com.fr.swift.exception.queue.SlaveExceptionInfoQueue;
import com.fr.swift.exception.service.ExceptionInfoService;
import com.fr.swift.log.SwiftLoggers;

/**
 * @author anchore
 * @date 2019/10/31
 */
@SwiftBean
public class ExceptionReporterImpl implements ExceptionReporter {
    @Override
    public void report(ExceptionContext context, ExceptionInfo.Type type) {
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