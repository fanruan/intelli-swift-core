package com.fr.bi.exception;

/**
 * This class created on 2016/7/28.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BIRuntimeException extends RuntimeException {
    public BIRuntimeException() {
    }

    public BIRuntimeException(String message) {
        super(message);
    }

    public BIRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BIRuntimeException(Throwable cause) {
        super(cause);
    }


}