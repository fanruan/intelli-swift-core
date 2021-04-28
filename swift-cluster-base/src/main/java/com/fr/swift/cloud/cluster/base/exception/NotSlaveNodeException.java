package com.fr.swift.cloud.cluster.base.exception;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/10/28
 */
public class NotSlaveNodeException extends RuntimeException {

    public NotSlaveNodeException() {
        this("current");
    }

    public NotSlaveNodeException(String node) {
        super(String.format("[%s] node is not slave!", node));
    }
}