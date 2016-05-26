package com.finebi.cube.conf.exception;

/**
 * This class created on 2016/5/25.
 *
 * @author Connery
 * @since 4.0
 */
public class BITableIDDuplicateException extends Exception {
    public BITableIDDuplicateException() {
    }

    public BITableIDDuplicateException(String message) {
        super(message);
    }

    public BITableIDDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public BITableIDDuplicateException(Throwable cause) {
        super(cause);
    }
}
