package com.finebi.cube.exception;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BIStatusDuplicateException extends Exception {

    public BIStatusDuplicateException() {
    }

    public BIStatusDuplicateException(String message) {
        super(message);
    }

    public BIStatusDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
