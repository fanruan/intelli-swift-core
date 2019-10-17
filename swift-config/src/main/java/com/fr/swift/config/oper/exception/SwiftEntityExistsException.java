package com.fr.swift.config.oper.exception;

/**
 * @author yee
 * @date 2018-12-05
 */
public class SwiftEntityExistsException extends RuntimeException {
    private static final long serialVersionUID = 185929654302650788L;

    public SwiftEntityExistsException(Throwable cause) {
        super(cause);
    }
}
