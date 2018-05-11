package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.queue.CubeTasks;
import com.fr.swift.cube.task.TaskKey;

/**
 * @author anchore
 * @date 2017/12/13
 */
public class CubeTaskKey implements TaskKey {
    private int round;
    private String name;
    private Operation operation;
    private String info;

    public CubeTaskKey(String name) {
        this(name, Operation.NULL);
    }

    public CubeTaskKey(String name, Operation operation) {
        this(CubeTasks.getCurrentRound(), name, operation);
    }

    public CubeTaskKey(int round, String name, Operation operation) {
        this.round = round;
        this.name = name;
        this.operation = operation;
    }

    public CubeTaskKey(String name, String info) {
        this(name);
        this.info = info;
    }

    public CubeTaskKey(String name, Operation operation, String info) {
        this(name, operation);
        this.info = info;
    }

    public CubeTaskKey(int round, String name, Operation operation, String info) {
        this(round, name, operation);
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

        CubeTaskKey that = (CubeTaskKey) o;

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