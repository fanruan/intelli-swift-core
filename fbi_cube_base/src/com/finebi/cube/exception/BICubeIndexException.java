package com.finebi.cube.exception;

/**
 * This class created on 2016/3/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeIndexException extends Exception {
    private static final long serialVersionUID = -2615724964299086320L;

    public BICubeIndexException(String message) {
        super(message);
    }

    public BICubeIndexException() {
    }

    public BICubeIndexException(String message, Throwable cause) {
        super(message, cause);
    }
}
