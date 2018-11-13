package com.fr.swift.service;

import com.fr.swift.property.SwiftProperty;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class created on 2018/7/18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractSwiftManager implements SwiftManager {

    protected SwiftProperty swiftProperty = SwiftProperty.getProperty();

    protected volatile boolean running = false;

    protected Lock lock = new ReentrantLock();

    @Override
    public void startUp() throws Exception {
        installService();
        running = true;
    }

    @Override
    public void shutDown() throws Exception {
        uninstallService();
        running = false;
    }

    protected abstract void installService() throws Exception;

    protected abstract void uninstallService() throws Exception;

    @Override
    public boolean isRunning() {
        return running;
    }
}
