package com.fr.swift.jdbc.exception;

/**
 * @author yee
 * @date 2018/11/19
 */
class ConnectionTimeoutException extends RuntimeException {
    ConnectionTimeoutException(String msg) {
        super(msg);
    }

    ConnectionTimeoutException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
