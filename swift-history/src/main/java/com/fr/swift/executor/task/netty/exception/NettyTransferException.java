package com.fr.swift.executor.task.netty.exception;

/**
 * @author Hoky
 * @date 2020/12/18
 */
public class NettyTransferException extends RuntimeException {
    public NettyTransferException() {
        super("Netty transfer failed");
    }
}
