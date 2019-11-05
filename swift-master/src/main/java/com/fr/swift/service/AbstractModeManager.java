package com.fr.swift.service;

import com.fr.swift.executor.ExecutorManager;
import com.fr.swift.executor.dispatcher.TaskDispatcher;

/**
 * This class created on 2019/3/12
 *
 * @author Lucifer
 * @description
 */
public abstract class AbstractModeManager extends AbstractSwiftManager {

    @Override
    public void startUp() throws Exception {
        super.startUp();
        TaskDispatcher.getInstance();
        ExecutorManager.getInstance().pullDBTask();
    }

    @Override
    public void shutDown() throws Exception {
        ExecutorManager.getInstance().clearTasks();
        super.shutDown();
    }
}
