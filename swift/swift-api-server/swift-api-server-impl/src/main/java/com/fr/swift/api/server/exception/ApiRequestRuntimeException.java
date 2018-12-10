package com.fr.swift.api.server.exception;

/**
 * This class created on 2018/12/10
 *
 * @author Lucifer
 * @description
 */
public class ApiRequestRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -507160369486415213L;

    private int statusCode;

    private Throwable throwable;

    public ApiRequestRuntimeException(int statusCode) {
        this.statusCode = statusCode;
    }

    public ApiRequestRuntimeException(int statusCode, String msg) {
        super(msg);
        this.statusCode = statusCode;
    }

    public ApiRequestRuntimeException(int statusCode, Throwable e) {
        super(e.getMessage());
        this.statusCode = statusCode;
        this.throwable = e;
    }

    public ApiRequestRuntimeException(int statusCode, String msg, Throwable e) {
        super(msg);
        this.statusCode = statusCode;
        this.throwable = e;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
