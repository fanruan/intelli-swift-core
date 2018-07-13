package com.fr.swift.task.impl;

import com.fr.swift.task.TaskResult;

import java.io.Serializable;

/**
 * @author anchore
 * @date 2018/5/12
 */
public class TaskResultImpl implements TaskResult, Serializable {
    private static final long serialVersionUID = -4651463521763875644L;
    private Type type;
    private Exception cause;

    public TaskResultImpl(Type type) {
        this(type, null);
    }

    public TaskResultImpl(Type type, Exception cause) {
        this.type = type;
        this.cause = cause;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Exception getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}