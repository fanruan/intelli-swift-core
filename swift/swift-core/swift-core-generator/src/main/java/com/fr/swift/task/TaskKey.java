package com.fr.swift.task;

/**
 * @author anchore
 * @date 2017/12/15
 */
public interface TaskKey {
    int getRound();

    String name();

    Operation operation();

    String info();
}