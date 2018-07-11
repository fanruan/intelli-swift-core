package com.fr.swift.task.impl;

import com.fr.swift.task.Operation;
import com.fr.swift.task.TaskKey;

import java.io.Serializable;

/**
 * @author anchore
 * @date 2017/12/13
 */
public class SwiftTaskKey implements TaskKey, Serializable {
    private static final long serialVersionUID = 2126126448338496452L;

    private final int round;

    private final String name;

    private final Operation operation;

    private String info;

    public SwiftTaskKey(int round, String name, Operation operation) {
        this(round, name, operation, null);
    }

    public SwiftTaskKey(int round, String name, Operation operation, String info) {
        this.round = round;
        this.name = name;
        this.operation = operation;
        this.info = info;
    }

    @Override
    public int getRound() {
        return round;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Operation operation() {
        return operation;
    }

    @Override
    public String info() {
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SwiftTaskKey that = (SwiftTaskKey) o;

        if (round != that.round) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return operation == that.operation;
    }

    @Override
    public int hashCode() {
        int result = round;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (operation != null ? operation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("{%d# %s, %s}", round, name, operation);
    }
}