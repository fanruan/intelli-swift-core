package com.finebi.cube.exception;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BIRegisterIsForbiddenException extends Exception {
    private static final long serialVersionUID = 2635840024308093664L;

    public BIRegisterIsForbiddenException() {
    }

    public BIRegisterIsForbiddenException(String message) {
        super(message);
    }
}
