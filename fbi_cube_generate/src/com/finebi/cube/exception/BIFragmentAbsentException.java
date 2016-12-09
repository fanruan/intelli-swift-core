package com.finebi.cube.exception;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFragmentAbsentException extends Exception {
    private static final long serialVersionUID = -3830673718010133635L;

    public BIFragmentAbsentException() {
    }

    public BIFragmentAbsentException(String message) {
        super(message);
    }
}
