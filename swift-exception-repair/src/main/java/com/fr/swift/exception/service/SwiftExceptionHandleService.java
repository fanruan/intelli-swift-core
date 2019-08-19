package com.fr.swift.exception.service;

import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.handler.ExceptionHandleTemplate;
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

    private static final long serialVersionUID = -3571316445157286314L;

    private ExceptionHandlerRegistry registry = ExceptionHandlerRegistry.getInstance();

    @Override
    public void handleException(ExceptionInfo info) {
        new ExceptionHandleTemplate(registry.getHandler(info.getType())).handleException(info);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.EXCEPTION;
    }
}
