package com.fr.swift.executor.exception;

import com.fr.swift.executor.task.ExecutorTask;

/**
 * This class created on 2019/3/5
 *
 * @author Lucifer
 * @description
 */
public class NotDBTaskExecption extends Exception {

    private static final long serialVersionUID = -1580965429579209356L;

    public NotDBTaskExecption(ExecutorTask executorTask) {
        super(executorTask.toString());
    }
}