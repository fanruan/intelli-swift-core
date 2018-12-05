package com.fr.swift.config.oper.exception;

/**
 * @author yee
 * @date 2018-12-04
 */
public class SwiftConstraintViolationException extends RuntimeException {
    private static final long serialVersionUID = 7345003502050440481L;

    public SwiftConstraintViolationException(Throwable cause) {
        super(cause);
    }
}
