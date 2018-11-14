package com.fr.swift.task.service;

import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * This class created on 2018/7/13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftServiceCallable<T> extends FutureTask<T> implements ServiceCallable<T> {

    private SourceKey sourceKey;
    private ServiceTaskType type;
    private List<ServiceTaskListener> serviceTaskListeners;

    public SwiftServiceCallable(SourceKey sourceKey, ServiceTaskType type, Callable<T> callable) {
        super(callable);
        this.sourceKey = sourceKey;
        this.type = type;
        this.serviceTaskListeners = new ArrayList<ServiceTaskListener>();
    }

    @Override
    protected void done() {
        finishJob();
    }

    @Override
    public SourceKey getKey() {
        return sourceKey;
    }

    @Override
    public ServiceTaskType getType() {
        return type;
    }

    private void finishJob() {
        for (ServiceTaskListener taskListener : serviceTaskListeners) {
            taskListener.handlerEvent(this);
        }
    }

    @Override
    public ServiceCallable addListener(ServiceTaskListener listener) {
        serviceTaskListeners.add(listener);
        return this;
    }
}
