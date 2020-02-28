package com.fr.swift.exception.handler;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoType;
import com.fr.swift.exception.PushSegmentExceptionContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.structure.Pair;

import java.util.concurrent.TimeUnit;

/**
 * @author anner
 * @this class created on date 2019/8/23
 * @description master向其他节点同步seg信息失败后的异常处理
 */
@SwiftBean
@RegisterExceptionHandler
public class MasterPushSegExceptionHandler implements ExceptionHandler {


    private final int LOOPS = 3;

    @Override
    public boolean handleException(ExceptionInfo exceptionInfo) {
        PushSegmentExceptionContext exceptionContext = (PushSegmentExceptionContext) exceptionInfo.getContext();
        for (int i = 0; i < LOOPS; i++) {
            if (retryPush(exceptionContext)) {
                return true;
            } else {
                //处理异常的线程延时三秒
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    SwiftLoggers.getLogger().warn(e);
                }
            }
        }
        return false;
    }

    private boolean retryPush(PushSegmentExceptionContext exceptionContext) {
        SegmentLocationInfo locationInfo = exceptionContext.getInfo();
        Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo> pair = Pair.of(SegmentLocationInfo.UpdateType.PART, locationInfo);
        try {
            //master重新发送一下同步seg的请求
            ServiceContext serviceContext = ProxySelector.getInstance().getFactory().getProxy(ServiceContext.class);
            serviceContext.updateSegmentInfo(pair.getValue(), pair.getKey());
            return true;
        } catch (Exception exception) {
            SwiftLoggers.getLogger().warn("Cannot sync native segment info to slave again!", exception);
            return false;
        }
    }

    @Override
    public ExceptionInfo.Type getExceptionInfoType() {
        return ExceptionInfoType.MASTER_PUSH_SEGMENT;
    }
}
