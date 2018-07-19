package com.fr.swift.netty.rpc.exception;

/**
 * This class created on 2018/6/8
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ServiceInvalidException extends Exception {

    private static final long serialVersionUID = -6337407687045559283L;

    public ServiceInvalidException() {
    }

    public ServiceInvalidException(String message) {
        super(message);
    }

    public ServiceInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceInvalidException(Throwable cause) {
        super(cause);
    }

    public ServiceInvalidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
