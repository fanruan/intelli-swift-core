package com.fr.swift.cloud.executor.task.netty.exception;

/**
 * @author Hoky
 * @date 2020/12/24
 */
public class NettyTransferOvertimeException extends RuntimeException {
    public NettyTransferOvertimeException() {
        super("Netty transfer overtime!");
    }
}
