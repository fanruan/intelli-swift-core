package com.fr.swift.quartz.service;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/9/3
 * TODO: 2020/9/3  move to core or base module
 */
public interface LifeCycle {

    void startup() throws LifeCycleException;

    void shutdown() throws LifeCycleException;

    boolean isStarted();
}
