package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.TaskKey;

/**
 * @author anchore
 * @date 2017/12/13
 */
public class CubeTaskKey implements TaskKey {
    private String name;
    private Operation operation;

    public CubeTaskKey(String name) {
        this(name, Operation.NULL);
    }

    public CubeTaskKey(String name, Operation operation) {
        this.name = name;
        this.operation = operation;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CubeTaskKey that = (CubeTaskKey) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return operation == that.operation;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (operation != null ? operation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CubeTaskKey{" +
                "name='" + name + '\'' +
                ", operation=" + operation +
                '}';
    }
}