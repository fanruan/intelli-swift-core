package com.fr.swift.exception.reporter;

import com.fr.swift.exception.ExceptionContext;
import com.fr.swift.exception.ExceptionInfo;

/**
 * @author Marvin
 * @date 8/13/2019
 * @description
 * @since swift 1.1
 */
public interface ExceptionReporter {

    /**
     * 包装，持久化异常，添加异常到队列中
     *
     * @param context
     */
    void report(ExceptionContext context, ExceptionInfo.Type type);
}