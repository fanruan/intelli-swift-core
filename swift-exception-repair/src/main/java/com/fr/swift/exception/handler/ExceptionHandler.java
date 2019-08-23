package com.fr.swift.exception.handler;

import com.fr.swift.exception.ExceptionInfo;

/**
 * @author Marvin
 * @date 8/9/2019
 * @description
 * @since swift 1.1
 */
public interface ExceptionHandler {

    /**
     * 处理异常的方法
     *
     * @param exceptionInfo exception info
     * @return 处理结果
     */
    boolean handleException(ExceptionInfo exceptionInfo);

    /**
     * 异常类型
     *
     * @return type
     */
    ExceptionInfo.Type getExceptionInfoType();
}
