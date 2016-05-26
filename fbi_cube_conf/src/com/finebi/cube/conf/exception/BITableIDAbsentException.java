package com.finebi.cube.conf.exception;

/**
 * This class created on 2016/5/25.
 *
 * @author Connery
 * @since 4.0
 */
public class BITableIDAbsentException extends Exception {
    public BITableIDAbsentException() {
    }

    public BITableIDAbsentException(String message) {
        super(message);
    }

    public BITableIDAbsentException(String message, Throwable cause) {
        super(message, cause);
    }

    public BITableIDAbsentException(Throwable cause) {
        super(cause);
    }
}
