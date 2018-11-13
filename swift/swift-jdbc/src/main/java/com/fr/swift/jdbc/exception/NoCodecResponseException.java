package com.fr.swift.jdbc.exception;

/**
 * @author yee
 * @date 2018/8/26
 */
public class NoCodecResponseException extends RpcException {
    public NoCodecResponseException() {
        super("Cannot find response");
    }

    public NoCodecResponseException(Throwable e) {
        super(e);
    }
}
