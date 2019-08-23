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
     * @param exceptionInfo
     */
    void handleException(ExceptionInfo exceptionInfo);

    /**
     * 评估处理结果
     *
     * @return
     */
    boolean evaluate();

    /**
     * 异常类型
     *
     * @return
     */
    ExceptionInfo.Type getExceptionInfoType();
}
