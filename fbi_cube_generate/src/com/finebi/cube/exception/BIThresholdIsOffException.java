package com.finebi.cube.exception;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BIThresholdIsOffException extends Exception {
    private static final long serialVersionUID = 2242056678482919609L;

    public BIThresholdIsOffException() {
    }

    public BIThresholdIsOffException(String message) {
        super(message);
    }
}
