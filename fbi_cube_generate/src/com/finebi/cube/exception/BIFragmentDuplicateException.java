package com.finebi.cube.exception;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFragmentDuplicateException extends Exception {
    private static final long serialVersionUID = 2691047486899522938L;

    public BIFragmentDuplicateException() {
    }

    public BIFragmentDuplicateException(String message) {
        super(message);
    }

    public BIFragmentDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
