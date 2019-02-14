package com.fr.swift.task.service;

import java.util.concurrent.Future;

/**
 * This class created on 2018/7/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ServiceTaskExecutor {

    <T> Future<T> submit(ServiceCallable<T> serviceCallable) throws InterruptedException;

    void registerQueue(String name, ServiceBlockingQueue queue);

    void unRegisterQueue(String name);
}
