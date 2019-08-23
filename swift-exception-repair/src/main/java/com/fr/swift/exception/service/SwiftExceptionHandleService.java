package com.fr.swift.exception.service;

import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoBean;
import com.fr.swift.exception.handler.ExceptionHandleTemplate;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.AbstractSwiftService;
import com.fr.swift.service.ServiceType;

/**
 * 节点处理异常的服务
 *
 * @author Marvin
 * @date 8/9/2019
 * @description
 * @since swift 1.1
 */
@SwiftBean
@ProxyService(ExceptionHandleService.class)
public class SwiftExceptionHandleService extends AbstractSwiftService implements ExceptionHandleService {

    @SwiftAutoWired
    private ExceptionInfoService infoService;

    private static final long serialVersionUID = -3571316445157286314L;

    private ExceptionHandlerRegistry registry = ExceptionHandlerRegistry.getInstance();

    @Override
    public void handleException(ExceptionInfo info) {
        updateExceptionInfo(info);
        new ExceptionHandleTemplate(registry.getHandler(info.getType())).handleException(info);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.EXCEPTION;
    }

    private void updateExceptionInfo(ExceptionInfo info) {
        String currentId = ClusterSelector.getInstance().getFactory().getCurrentId();
        ExceptionInfoBean bean = ExceptionInfoBean.builder(info)
                .setOperateNodeId(currentId)
                .setState(ExceptionInfo.State.PENDING).build();
        infoService.removeExceptionInfo(info.getId());
        infoService.maintain(bean);
    }
}
