package com.fr.swift.cube.task;

import com.fr.swift.cube.task.impl.Operation;

import java.io.Serializable;

/**
 * @author anchore
 * @date 2017/12/15
 */
public interface TaskKey extends Serializable {
    int getRound();

    String name();

    Operation operation();

    String info();
}