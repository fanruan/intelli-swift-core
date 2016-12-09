package com.finebi.cube.exception;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BITopicAbsentException extends Exception {

    private static final long serialVersionUID = -7399488885360252069L;

    public BITopicAbsentException() {
    }

    public BITopicAbsentException(String message) {
        super(message);
    }
}
