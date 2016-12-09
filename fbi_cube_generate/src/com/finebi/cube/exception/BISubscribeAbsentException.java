package com.finebi.cube.exception;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BISubscribeAbsentException extends Exception {
    private static final long serialVersionUID = 6367246590601499811L;

    public BISubscribeAbsentException() {
    }

    public BISubscribeAbsentException(String message) {
        super(message);
    }
}
