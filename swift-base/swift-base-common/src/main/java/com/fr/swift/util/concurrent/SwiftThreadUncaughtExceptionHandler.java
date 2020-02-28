package com.fr.swift.util.concurrent;

import com.fr.swift.log.SwiftLoggers;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * @author anchore
 * @date 2019/8/26
 */
public enum SwiftThreadUncaughtExceptionHandler implements UncaughtExceptionHandler {
    //
    INSTANCE;

    /**
     * pure function
     *
     * @param t thread
     * @param e error
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        SwiftLoggers.getLogger().error("{} encountered error", t, e);
    }
}