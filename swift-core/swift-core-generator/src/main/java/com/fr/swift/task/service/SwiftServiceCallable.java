package com.fr.swift.task.service;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/7/13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class SwiftServiceCallable implements ServiceCallable {

    private SourceKey sourceKey;
    private ServiceTaskType type;
    private List<ServiceTaskListener> serviceTaskListeners;


    public SwiftServiceCallable(SourceKey sourceKey, ServiceTaskType type) {
        this.sourceKey = sourceKey;
        this.type = type;
        this.serviceTaskListeners = new ArrayList<ServiceTaskListener>();
    }

    @Override
    public Object call() {
        try {
            doJob();
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        } finally {
            finishJob();
        }
    }

    @Override
    public SourceKey getKey() {
        return sourceKey;
    }

    @Override
    public ServiceTaskType getType() {
        return type;
    }

    protected void finishJob() {
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
