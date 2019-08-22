package com.fr.swift.exception.handler;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoType;

/**
 * 这个类用来测试异常处理流程
 *
 * @author Marvin
 * @date 8/19/2019
 * @description
 * @since swift 1.1
 */
@SwiftBean
@RegisterExceptionHandler
public class DummyHandler implements ExceptionHandler {
    @Override
    public void handleException(ExceptionInfo exceptionInfo) {

    }

    @Override
    public boolean evaluate() {
        return false;
    }

    @Override
    public ExceptionInfo.Type getExceptionInfoType() {
        return ExceptionInfoType.UPLOAD_SEGMENT;
    }
}
