package com.fr.swift.segment.operator.insert;

/**
 * This class created on 2018/6/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RealtimeInsertException extends Exception {

    public RealtimeInsertException() {
        super();
    }

    public RealtimeInsertException(String message) {
        super(message);
    }

    public RealtimeInsertException(String message, Throwable cause) {
        super(message, cause);
    }

    public RealtimeInsertException(Throwable cause) {
        super(cause);
    }

    protected RealtimeInsertException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
