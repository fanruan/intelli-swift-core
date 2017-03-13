package com.finebi.cube.exception;

/**
 * This class created on 2016/3/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeColumnAbsentException extends Exception {
    private static final long serialVersionUID = -1807874778781668929L;

    public BICubeColumnAbsentException(String message) {
        super(message);
    }

    public BICubeColumnAbsentException() {
    }
}
