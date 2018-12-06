package com.fr.swift.config.oper.exception;

/**
 * @author yee
 * @date 2018-12-04
 */
public class SwiftNonUniqueObjectException extends RuntimeException {
    private static final long serialVersionUID = 1608280131421426586L;

    public SwiftNonUniqueObjectException(Throwable cause) {
        super(cause);
    }
}
