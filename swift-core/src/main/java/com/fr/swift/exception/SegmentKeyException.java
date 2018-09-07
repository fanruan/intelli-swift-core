package com.fr.swift.exception;

/**
 * This class created on 2018/7/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SegmentKeyException extends Exception {

    public SegmentKeyException() {
        super();
    }

    public SegmentKeyException(String message) {
        super(message);
    }

    public SegmentKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public SegmentKeyException(Throwable cause) {
        super(cause);
    }

    protected SegmentKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
