package com.fr.swift.exception.consumer;

import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.queue.ExceptionInfoQueue;
import com.fr.swift.exception.service.ExceptionHandleService;
import com.fr.swift.log.SwiftLoggers;

/**
 * @author Marvin
 * @date 8/13/2019
 * @description
 * @since swift 1.1UP
 */
public class ExceptionInfoConsumer implements Runnable {

    private ExceptionHandleService exceptionHandleService;

    private ExceptionInfoQueue queue;

    public ExceptionInfoConsumer(ExceptionInfoQueue queue, ExceptionHandleService exceptionHandleService) {
        this.queue = queue;
        this.exceptionHandleService = exceptionHandleService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ExceptionInfo info = queue.take();
                exceptionHandleService.handleException(info);
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }
}
