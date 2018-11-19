package com.fr.swift.task.service;

/**
 * This class created on 2018/7/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ServiceTaskExecutor {

    void submit(ServiceCallable serviceCallable) throws InterruptedException;

    void registerQueue(String name, ServiceBlockingQueue queue);

    void unRegisterQueue(String name);

}
