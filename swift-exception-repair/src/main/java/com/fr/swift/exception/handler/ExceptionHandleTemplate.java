package com.fr.swift.exception.handler;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoBean;
import com.fr.swift.exception.event.ExceptionStateRpcEvent;
import com.fr.swift.exception.service.ExceptionInfoService;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.listener.RemoteSender;

/**
 * @author Marvin
 * @date 8/13/2019
 * @description
 * @since swift 1.1
 */
public class ExceptionHandleTemplate {

    private ExceptionInfoService infoService = SwiftContext.get().getBean(ExceptionInfoService.class);

    private ExceptionHandler exceptionHandler;

    public ExceptionHandleTemplate(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public void handleException(ExceptionInfo exceptionInfo) {
        boolean success = false;
        try {
            success = exceptionHandler.handleException(exceptionInfo);
        } finally {
            if (success) {
                infoService.removeExceptionInfo(exceptionInfo.getId());
                handleExceptionResult(exceptionInfo, ExceptionInfo.State.SOLVED);
            } else {
                handleExceptionResult(exceptionInfo, ExceptionInfo.State.UNSOLVED);
            }
        }
    }

    void handleExceptionResult(ExceptionInfo info, ExceptionInfo.State state) {
        String currentId = ClusterSelector.getInstance().getFactory().getCurrentId();
        ExceptionInfoBean bean = ExceptionInfoBean.builder(info)
                .setOperateNodeId(currentId)
                .setState(state).build();
        SwiftRpcEvent event = new ExceptionStateRpcEvent(bean);
        ProxySelector.getProxy(RemoteSender.class).trigger(event);
    }
}