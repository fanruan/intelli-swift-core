package com.finebi.cube.exception;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BIThresholdUnsatisfiedException extends Exception {
    private static final long serialVersionUID = -3472804150537983L;

    public BIThresholdUnsatisfiedException() {
    }

    public BIThresholdUnsatisfiedException(String message) {
        super(message);
    }
}
