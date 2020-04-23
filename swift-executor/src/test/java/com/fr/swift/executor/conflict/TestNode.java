package com.fr.swift.executor.conflict;

/**
 * @author lucifer
 * @date 2020/4/13
 * @description
 * @since swift-log 10.0.5
 */
public class TestNode {

    private int priority;
    private long time;
    private String name;

    public TestNode(int priority, String name) {
        this.priority = priority;
        this.time = System.currentTimeMillis();
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "TestNode{" +
                "name='" + name + '\'' +
                '}';
    }
}