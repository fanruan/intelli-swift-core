package com.fr.swift.api.rpc.exception;

/**
 * @author yee
 * @date 2018/9/6
 */
public class ConnectException extends RuntimeException {
    public ConnectException(String address, Throwable throwable) {
        super("Connect to " + address + " with error!", throwable);
    }
}
