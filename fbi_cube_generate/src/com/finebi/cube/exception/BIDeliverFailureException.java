package com.finebi.cube.exception;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BIDeliverFailureException extends Exception {
    public BIDeliverFailureException() {
    }

    public BIDeliverFailureException(String message) {
        super(message);
    }

    public BIDeliverFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
