package com.fr.swift.jdbc.exception;

/**
 * @author yee
 * @date 2018/8/26
 */
public class RpcException extends RuntimeException {
    public RpcException(String msg) {
        super(msg);
    }

    public RpcException(Throwable e) {
        super(e);
    }
}
