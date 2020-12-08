package com.fr.swift.basics.exception;

/**
 * @author Heng.J
 * @date 2020/11/5
 * @description
 * @since swift-1.2.0
 */
public class TargetNodeOfflineException extends RuntimeException {

    public TargetNodeOfflineException() {
        this("current");
    }

    public TargetNodeOfflineException(String node) {
        super(String.format("[%s] node is offline!", node));
    }
}
