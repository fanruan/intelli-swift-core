package com.fr.swift.task.service;

import com.fr.swift.source.SourceKey;

import java.util.concurrent.Callable;

/**
 * This class created on 2018/7/13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ServiceCallable extends Callable {

    SourceKey getKey();

    ServiceTaskType getType();

    void doJob() throws Exception;

    ServiceCallable addListener(ServiceTaskListener listener);

}
