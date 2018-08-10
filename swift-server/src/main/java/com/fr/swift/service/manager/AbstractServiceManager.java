package com.fr.swift.service.manager;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class created on 2018/8/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractServiceManager<T> implements ServiceManager<T> {

    protected ReentrantLock lock = new ReentrantLock();

    @Override
    public void registerService(T service) throws Exception {
        registerService(Collections.singletonList(service));
    }

    @Override
    public void unregisterService(T service) throws Exception {
        unregisterService(Collections.singletonList(service));
    }

    @Override
    public abstract void registerService(List<T> serviceList) throws Exception;

    @Override
    public abstract void unregisterService(List<T> serviceList) throws Exception;
}
