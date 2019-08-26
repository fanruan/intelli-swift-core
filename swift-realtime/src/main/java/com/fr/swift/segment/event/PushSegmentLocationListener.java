package com.fr.swift.segment.event;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.global.PushSegLocationRpcEvent;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoBean;
import com.fr.swift.exception.ExceptionInfoType;
import com.fr.swift.exception.PushSegmentExceptionContext;
import com.fr.swift.exception.reporter.ExceptionReporter;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.listener.RemoteSender;

import java.io.Serializable;

/**
 * @author anchore
 * @date 2018/12/28
 */
public class PushSegmentLocationListener extends BaseSegmentLocationListener {

    @Override
    Serializable trigger(SegmentLocationInfo segLocations) {
        try {
            return ProxySelector.getProxy(RemoteSender.class).trigger(new PushSegLocationRpcEvent(segLocations));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            reportPushSegException(segLocations);
        }
        return null;
    }

    static {
        SwiftEventDispatcher.listen(SyncSegmentLocationEvent.PUSH_SEG, new PushSegmentLocationListener());
    }

    private PushSegmentLocationListener() {
    }

    public static void listen() {
    }

    //报告异常的方法抽出来，避免影响原有的逻辑的展示
    private void reportPushSegException(SegmentLocationInfo exceptionContext) {
        ExceptionInfo exceptionInfo = new ExceptionInfoBean.Builder()
                .setContext(new PushSegmentExceptionContext(exceptionContext))
                .setType(ExceptionInfoType.SLAVE_PUSH_SEGMENT)
                .setNowAndHere().build();
        ExceptionReporter.report(exceptionInfo);
    }
}