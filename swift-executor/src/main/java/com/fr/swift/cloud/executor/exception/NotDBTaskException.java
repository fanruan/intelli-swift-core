package com.fr.swift.cloud.executor.exception;

import com.fr.swift.cloud.executor.task.ExecutorTask;

/**
 * This class created on 2019/3/5
 *
 * @author Lucifer
 * @description
 */
public class NotDBTaskException extends Exception {

    private static final long serialVersionUID = -1580965429579209356L;

    public NotDBTaskException(ExecutorTask executorTask) {
        super(executorTask.toString());
    }
}
