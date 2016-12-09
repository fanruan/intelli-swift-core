package com.finebi.cube.exception;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BISubscribeDuplicateException extends Exception {
    private static final long serialVersionUID = 876618528144521820L;

    public BISubscribeDuplicateException() {
    }

    public BISubscribeDuplicateException(String message) {
        super(message);
    }
}
