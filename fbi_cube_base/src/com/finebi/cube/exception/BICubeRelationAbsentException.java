package com.finebi.cube.exception;

/**
 * This class created on 2016/3/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeRelationAbsentException extends Exception {
    private static final long serialVersionUID = 9137980565122686549L;

    public BICubeRelationAbsentException(String message) {
        super(message);
    }

    public BICubeRelationAbsentException(String message, Throwable cause) {
        super(message, cause);
    }

    public BICubeRelationAbsentException() {
    }
}
