package com.finebi.cube.exception;

/**
 * This class created on 2016/3/31.
 *
 * @author Connery
 * @since 4.0
 */
public class IllegalRelationPathException extends Exception {
    public IllegalRelationPathException() {
    }

    public IllegalRelationPathException(String message) {
        super(message);
    }

    public IllegalRelationPathException(String message, Throwable cause) {
        super(message, cause);
    }
}
