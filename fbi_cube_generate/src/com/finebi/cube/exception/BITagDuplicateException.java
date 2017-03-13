package com.finebi.cube.exception;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BITagDuplicateException extends Exception {
    private static final long serialVersionUID = -8094465303672390316L;

    public BITagDuplicateException() {
    }

    public BITagDuplicateException(String message) {
        super(message);
    }
}
