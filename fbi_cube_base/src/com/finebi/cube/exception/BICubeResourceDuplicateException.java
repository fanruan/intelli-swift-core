package com.finebi.cube.exception;

/**
 * This class created on 2016/3/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeResourceDuplicateException extends Exception {
    private static final long serialVersionUID = 5497182791993098357L;

    public BICubeResourceDuplicateException(String message) {
        super(message);
    }

    public BICubeResourceDuplicateException() {
    }
}
