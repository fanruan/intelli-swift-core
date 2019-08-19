package com.fr.swift.exception.service;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.handler.ExceptionProcessHandler;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.service.SwiftService;

/**
 * @author Marvin
 * @date 8/9/2019
 * @description
 * @since swift 1.1
 */
public interface ExceptionHandleService extends SwiftService {
    /**
     * 节点处理异常的服务
     *
     * @param exceptionInfo
     * @return
     * @throws Exception
     */
    @InvokeMethod(value = ExceptionProcessHandler.class, target = Target.ALL)
    void handleException(ExceptionInfo exceptionInfo);
}
