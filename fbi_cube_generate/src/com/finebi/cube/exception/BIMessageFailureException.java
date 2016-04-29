package com.finebi.cube.exception;

/**
 * This class created on 2016/3/22.
 *
 * @author Connery
 * @since 4.0
 */
public class BIMessageFailureException extends Exception {
    public BIMessageFailureException() {
    }

    public BIMessageFailureException(String message) {
        super(message);
    }

    public BIMessageFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
