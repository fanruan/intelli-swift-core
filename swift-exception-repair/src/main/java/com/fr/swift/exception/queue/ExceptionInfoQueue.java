package com.fr.swift.exception.queue;

import com.fr.swift.exception.ExceptionInfo;

/**
 * @author Marvin
 * @date 8/14/2019
 * @description
 * @since swift 1.1
 */
public interface ExceptionInfoQueue {

    /**
     * 从数据库中取出异常信息加入队列
     */
    void initExceptionInfoQueue();

    boolean offer(ExceptionInfo info);

    ExceptionInfo take() throws InterruptedException;

}
