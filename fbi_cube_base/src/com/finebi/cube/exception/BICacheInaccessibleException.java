package com.finebi.cube.exception;

/**
 * This class created on 2016/3/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICacheInaccessibleException extends Exception {
    private static final long serialVersionUID = 8495899405912287302L;

    public BICacheInaccessibleException(String message) {
        super(message);
    }

    public BICacheInaccessibleException() {
    }
}
