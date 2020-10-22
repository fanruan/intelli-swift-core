package com.fr.swift.api.server.exception;

/**
 * @author Hoky
 * @date 2020/10/21
 */
public class ApiUserPasswordException extends RuntimeException {

    private Throwable throwable;

    public ApiUserPasswordException(String msg) {
        super(msg);
    }

    public ApiUserPasswordException(Throwable e) {
        super(e.getMessage());
        this.throwable = e;
    }

    public ApiUserPasswordException(String msg, Throwable e) {
        super(msg);
        this.throwable = e;
    }
}
