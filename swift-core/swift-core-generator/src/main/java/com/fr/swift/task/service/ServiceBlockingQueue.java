package com.fr.swift.task.service;

import com.fr.swift.source.SourceKey;

import java.util.concurrent.BlockingQueue;

/**
 * This class created on 2018/11/16
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ServiceBlockingQueue extends BlockingQueue<ServiceCallable> {

    void decreaseNumBySourceKey(SourceKey sourceKey);

    Integer getNumBySourceKey(SourceKey sourceKey);

}
