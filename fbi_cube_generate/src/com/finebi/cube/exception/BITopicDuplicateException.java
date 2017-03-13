package com.finebi.cube.exception;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BITopicDuplicateException extends Exception {

    private static final long serialVersionUID = 8726596822656864595L;

    public BITopicDuplicateException() {
    }

    public BITopicDuplicateException(String message) {
        super(message);
    }

    public BITopicDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
