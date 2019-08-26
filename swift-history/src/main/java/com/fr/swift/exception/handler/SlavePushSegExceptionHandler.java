package com.fr.swift.exception.handler;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.event.global.PushSegLocationRpcEvent;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoType;
import com.fr.swift.exception.PushSegmentExceptionContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.listener.RemoteSender;

import java.util.concurrent.TimeUnit;

/**
 * @author anner
 * @this class created on date 2019/8/22
 * @description 子节点处理同步seg失败的异常
 */
@SwiftBean
@RegisterExceptionHandler
public class SlavePushSegExceptionHandler implements ExceptionHandler {


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
        SegmentLocationInfo info = exceptionContext.getInfo();
        try {
            //子节点重新发送一下同步seg的请求到master
            ProxySelector.getInstance().getFactory().getProxy(RemoteSender.class).trigger(new PushSegLocationRpcEvent(info));
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("Cannot sync native segment info to server again!", e);
            return false;
        }
    }

    @Override
    public ExceptionInfo.Type getExceptionInfoType() {
        return ExceptionInfoType.SLAVE_PUSH_SEGMENT;
    }
}
