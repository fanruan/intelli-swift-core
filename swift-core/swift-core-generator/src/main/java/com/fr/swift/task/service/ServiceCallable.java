package com.fr.swift.task.service;

import com.fr.swift.source.SourceKey;

import java.util.concurrent.RunnableFuture;

/**
 * This class created on 2018/7/13
 *d
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ServiceCallable<T> extends RunnableFuture<T> {

    SourceKey getKey();

    ServiceTaskType getType();

    ServiceCallable addListener(ServiceTaskListener listener);

}
